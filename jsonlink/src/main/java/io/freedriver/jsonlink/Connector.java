package io.freedriver.jsonlink;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.freedriver.jsonlink.jackson.JsonLinkModule;
import io.freedriver.jsonlink.jackson.schema.v1.Request;
import io.freedriver.jsonlink.jackson.schema.v1.Response;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.SECONDS;

public interface Connector extends AutoCloseable {
    Logger LOGGER = Logger.getLogger(Connector.class.getName());
    ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JsonLinkModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    /**
     * Send a request, receiving a response.
     */
    default Response send(Request request) throws ConnectorException {
        UUID requestId = makeRequest(request, Duration.of(2, SECONDS));
        return getResponse(requestId, Duration.of(2, SECONDS));
    }


    /**
     * Send a request, returning a Request Id by which to get a response.
     */
    UUID makeRequest(Request request, Duration maxWait) throws ConnectorException;

    /**
     * Block-waits for a response with a given request id to appear, then removes it from the response pool for return.
     */
    Response getResponse(UUID requestId, Duration maxWait) throws ConnectorException;

    /*
    default Response send(Request request) throws ConnectorException {
        try {
            UUID requestId = UUID.randomUUID();
            request.setRequestId(requestId);
            String json = MAPPER.writeValueAsString(request);
            LOGGER.finest("Sending Request: ");
            LOGGER.finest(json);
            return sendJSONRequest(json)
                    .map(r -> r.logAnyErrors(err -> LOGGER.warning("Error from board: " + err)))
                    .get();
        } catch (JsonProcessingException e) {
            throw new ConnectorException("Couldn't marshall JSON", e);
        }
    }

     */

    /**
     * Setup the board's UUID.
     */
    default UUID getUUID() throws ConnectorException {
        return Optional.of(new Request())
                .map(this::send)
                .map(Response::getUuid)
                .orElseGet(() -> send(new Request().newUuid()).getUuid());
    }


    default String getStatus() {
        return isClosed() ? "Closed" : "Opened";
    }

    String device();

    default Path devicePath() {
        return Paths.get(device());
    }

    boolean isClosed();
}
