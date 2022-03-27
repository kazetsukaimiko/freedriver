package io.freedriver.jsonlink;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.freedriver.jsonlink.jackson.schema.v1.Request;
import io.freedriver.jsonlink.jackson.schema.v1.Response;
import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.exception.SerialResourceTimeoutException;
import io.freedriver.serial.stream.api.SerialEntityStream;
import io.freedriver.serial.stream.api.accumulators.NewlineAccumulator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

public class SerialConnector implements Connector, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SerialConnector.class.getName());

    public static final Duration RESPONSE_EXPIRY = Duration.of(1, MINUTES);

    private final ExecutorService pool;
    private final String device;
    private final SerialEntityStream<Response> serialEntityStream;

    private UUID uuid;

    // Buffer to fetch responses
    private final Map<UUID, Response> responseMap = new ConcurrentHashMap<>();

    public SerialConnector(ExecutorService pool, String device, SerialEntityStream<Response> serialEntityStream) {
        this.pool = pool;
        this.device = device;
        this.serialEntityStream = serialEntityStream;
        LOGGER.info("Added Connector Device: " + device);
    }

    public SerialConnector(ExecutorService pool, SerialResource serialResource) {
        this(
                pool,
                serialResource.getName(),
                new SerialEntityStream<>(serialResource, new ResponseAccumulator())
        );
    }

    /*
    private Map<UUID, Response> getResponseMap() {
        synchronized (responseMap) {
            Set<UUID> expired = responseMap.values()
                    .stream()
                    .filter(response -> Instant.now().isAfter(response.getCreated().plus(RESPONSE_EXPIRY)))
                    .map(Response::getRequestId)
                    .collect(Collectors.toSet());
            expired.stream()
                    .peek(requestId -> LOGGER.warning("Request Id " + requestId + " (board " + responseMap.get(requestId).getUuid() + ") was never consumed")) // TODO: Event
                    .forEach(responseMap::remove);
            return responseMap;
        }
    }

     */

    @Override
    public UUID makeRequest(Request request, Duration maxWait) throws ConnectorException {
        Future<UUID> future = pool.submit(() -> {
            try {
                if (request.getRequestId() == null) {
                    request.setRequestId(UUID.randomUUID());
                    LOGGER.fine("Randomized requestId to: " + request.getRequestId());
                }
                String json = MAPPER.writeValueAsString(request);
                LOGGER.finer("Sending Request: ");
                sendJSONRequest(json);
                return request.getRequestId();
            } catch (JsonProcessingException | SerialResourceException e) {
                throw new ConnectorException("Couldn't marshall JSON", e);
            }
        });
        try {
            return future.get(maxWait.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new ConnectorException("Request failure: ", e);
        }
    }

    @Override
    public Response getResponse(UUID requestId, Duration maxWait) throws ConnectorException {
        Future<Response> future = pool.submit(() -> {
            synchronized (responseMap) {
                while (!responseMap.containsKey(requestId)) {
                    Response r = serialEntityStream.next();
                    responseMap.put(requestId, r);
                }
                return responseMap.get(requestId);
            }
        });
        try {
            return future.get(maxWait.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new ConnectorException("Request failure: ", e);
        }
    }

    @Override
    public String device() {
        return device;
    }

    private void sendJSONRequest(String requestJSON) throws ConnectorException {
        try {
            LOGGER.log(Level.FINEST, requestJSON);
            serialEntityStream.write(requestJSON.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ConnectorException("Couldn't consume JSON", e);
        }
    }

    @Override
    public boolean isClosed() {
        return serialEntityStream.isClosed();
    }


    @Override
    public UUID getUUID() throws ConnectorException {
        if (uuid == null) {
            uuid = Optional.of(new Request())
                    .map(this::send)
                    .map(Response::getUuid)
                    .orElseGet(() -> send(new Request().newUuid()).getUuid());
        }
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialConnector that = (SerialConnector) o;
        return Objects.equals(getUUID(), that.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID());
    }

    @Override
    public void close() throws Exception {
        LOGGER.log(Level.WARNING, "Closing serialEntityStream.");
        serialEntityStream.close();
    }
}
