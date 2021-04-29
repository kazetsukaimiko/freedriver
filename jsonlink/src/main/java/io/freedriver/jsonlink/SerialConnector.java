package io.freedriver.jsonlink;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.freedriver.jsonlink.jackson.schema.v1.Request;
import io.freedriver.jsonlink.jackson.schema.v1.Response;
import io.freedriver.serial.SerialResource;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.exception.SerialResourceTimeoutException;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

//import jssc.SerialPort;
//import jssc.SerialPortException;
//import jssc.SerialPortTimeoutException;

public class SerialConnector implements Connector, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SerialConnector.class.getName());
    public static final Duration RESPONSE_EXPIRY = Duration.of(1, MINUTES);

    private final String device;
    private final SerialResource serialResource;
    private final UUID uuid;

    private StringBuilder buffer = new StringBuilder();

    // Buffer to fetch responses
    private final Map<UUID, Response> responseMap = new ConcurrentHashMap<>();

    public SerialConnector(SerialResource serialResource) {
        this.device = serialResource.getName();
        this.serialResource = serialResource;
        this.uuid = Optional.of(new Request())
                .map(this::send)
                .map(Response::getUuid)
                .orElseGet(() -> send(new Request().newUuid()).getUuid());
        //.orElseGet(() -> failToGetUUID(serialResource));
        LOGGER.info("Added Connector Device: " + device + "; UUID: " + uuid);
    }

    private static UUID failToGetUUID(SerialResource serialResource) {
        try {
            LOGGER.warning("Failed to open SerialConnector for " + serialResource.getName());
            serialResource.close();
            return null;
        } catch (RuntimeException rte) {
            throw rte;
        } catch (Exception e) {
            throw new SerialResourceException("Failed to close; getting UUID", e);
        }
    }

    private Map<UUID, Response> getResponseMap() {
        Set<UUID> expired = responseMap.values()
                .stream()
                .filter(response -> Instant.now().isAfter(response.getCreated().plus(RESPONSE_EXPIRY)))
                .map(Response::getRequestId)
                .collect(Collectors.toSet());
        expired.stream()
            .peek(requestId -> LOGGER.warning("Request Id " + requestId + " (board "+responseMap.get(requestId).getUuid()+") was never consumed")) // TODO: Event
            .forEach(responseMap::remove);
        return responseMap;
    }

    @Override
    public Response send(Request request, Duration maxwait) throws ConnectorException {
        try {
            if (request.getRequestId() == null) {
                request.setRequestId(UUID.randomUUID());
                LOGGER.fine("Randomized requestId to: " + request.getRequestId());
            }
            String json = MAPPER.writeValueAsString(request);
            LOGGER.finer("Sending Request: ");
            sendJSONRequest(json);
            LOGGER.finer("Getting responses");
            return pollUntil(request.getRequestId(), maxwait)
                    .map(r -> r.logAnyErrors(err -> LOGGER.warning("Error from board: " + err)))
                    .orElseThrow(() -> new ConnectorException("Couldn't get response."));
        } catch (JsonProcessingException | SerialResourceException e) {
            throw new ConnectorException("Couldn't marshall JSON", e);
        }
    }

    @Override
    public String device() {
        return device;
    }

    private void sendJSONRequest(String requestJSON) throws ConnectorException {
        try {
            LOGGER.log(Level.FINEST, requestJSON);
            serialResource.write(requestJSON.getBytes());
        } catch (SerialResourceException e) {
            throw new ConnectorException("Couldn't consume JSON", e);
        }
    }

    @Override
    public boolean isClosed() {
        return !serialResource.isOpened();
    }


    private Optional<Response> pollUntil(UUID requestId, Duration maxwait) throws SerialResourceException {
        Instant start = Instant.now();
        while (true) {
            if (getResponseMap().containsKey(requestId)) {
                LOGGER.fine("Found request");
                return Optional.of(getResponseMap().remove(requestId));
            }
            Optional<String> json = pollUntilFinish(maxwait);
            if (json.isPresent()) {
                LOGGER.fine("Json Payload received.");
                String responseJSON = json.get();
                try {
                    Response response = MAPPER.readValue(responseJSON, Response.class);
                    LOGGER.fine("New response: " + response.getRequestId());
                    if (Objects.equals(requestId, response.getRequestId())) {
                        response.getError()
                                .forEach(error -> LOGGER
                                        .warning("Board Id ("+response.getUuid()+")returned error: \n"+error));
                        return Optional.of(response);
                    } else {
                        if (response.getRequestId() != null) {
                            getResponseMap().put(response.getRequestId(), response);
                        } else {
                            LOGGER.warning("Valid payload returned with no requestId.");
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new ConnectorException("Couldn't consume JSON", e);
                }
            }

            if (Instant.now().isAfter(start.plus(RESPONSE_EXPIRY))) {
                LOGGER.warning("Request expired");
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new ConnectorException("Sleep failure", e);
            }
        }
        return Optional.empty();
    }

    private Optional<String> pollUntilFinish(Duration maxwait) throws SerialResourceException {
        Instant start = Instant.now();
        boolean invalidBuffer = buffer.length() > 0 && !buffer.toString().startsWith("{");
        while (start.plus(maxwait).isAfter(Instant.now())) {
            Optional<String> response = poll(Duration.ofMillis(1000));
            if (response.isPresent()) {
                if (validate(response.get())) {
                    return response;
                } else {
                    LOGGER.warning("Invalid response: " + response);
                    if (!response.get().endsWith("\n")) {
                        int cleared = serialResource.clearToNewline();
                        LOGGER.warning("Cleared rest of buffer: " + cleared);
                    }
                }
            } else if (invalidBuffer && buffer.length() == 0) { // If we reset polling...
                return Optional.empty();
            }
        }
        throw new ConnectorTimeoutException("polling for message", maxwait);
    }

    private Optional<String> poll(Duration ofMillis) {
        try {
            return Optional.of(serialResource.nextLine(ofMillis));
        } catch (SerialResourceTimeoutException srte) {
            LOGGER.log(Level.WARNING, "Timed out polling for next line", srte);
        }
        return Optional.empty();
    }

    private static boolean validate(String input) {
        Map<Character, Integer> occurrenceMap = occurrenceMap(input);
        boolean valid =
                input.startsWith("{") && input.endsWith("}\n") && !input.contains(String.valueOf((char) 65533))
                    && occurrenceMap.getOrDefault("{", 0) == occurrenceMap.getOrDefault("}", 0)
                    && occurrenceMap.getOrDefault("[", 0) == occurrenceMap.getOrDefault("]", 0)
                ;
        if (!valid) {
            Connectors.getCallback()
                    .accept(input);
        }
        return valid;
    }

    private static Map<Character, Integer> occurrenceMap(String input) {
        Map<Character, Integer> om = new HashMap<>();
        if (input != null && !input.isEmpty()) {
            for (int i = 0; i < input.length(); i++) {
                Character key = input.charAt(i);
                if (!om.containsKey(key)) {
                    om.put(key, 1);
                } else {
                    om.put(key, om.get(key) + 1);
                }
            }
        }
        return om;
    }

    @Override
    public UUID getUUID() throws ConnectorException {
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
        if (!isClosed()) {
            LOGGER.log(Level.WARNING, "Closing serialPort.");
            poll(Duration.ofMillis(1000));
            serialResource.close();
        } else {
            LOGGER.log(Level.WARNING, "Tried to close serialPort, but was already closed.");
        }
    }
}
