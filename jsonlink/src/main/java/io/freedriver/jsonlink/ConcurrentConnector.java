package io.freedriver.jsonlink;

import io.freedriver.jsonlink.jackson.schema.v1.Request;
import io.freedriver.jsonlink.jackson.schema.v1.Response;

import java.time.Duration;
import java.util.UUID;

/**
 * Connector delegator that forces synchronization across all methods.
 */
public class ConcurrentConnector extends ConcurrentConnectorBase {
    public ConcurrentConnector(Connector delegate) {
        super(delegate);
    }

    @Override
    public UUID makeRequest(Request request, Duration maxWait) throws ConnectorException {
        return map(c -> c.makeRequest(request, maxWait));
    }

    @Override
    public Response getResponse(UUID requestId, Duration maxWait) throws ConnectorException {
        return map(c -> c.getResponse(requestId, maxWait));
    }

    @Override
    public String device() {
        return map(Connector::device);
    }

    @Override
    public boolean isClosed() {
        return map(Connector::isClosed);
    }

    @Override
    public void close() throws Exception {
        apply(Connector::close);
    }
}
