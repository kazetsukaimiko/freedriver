package io.freedriver.jsonlink;

import java.time.Duration;
import java.util.UUID;

import io.freedriver.jsonlink.api.v1.BoardRequest;
import io.freedriver.jsonlink.api.v1.BoardResponse;

/**
 * Connector delegator that forces synchronization across all methods.
 */
public class ConcurrentConnector extends ConcurrentConnectorBase {
    public ConcurrentConnector(Connector delegate) {
        super(delegate);
    }

    @Override
    public UUID makeRequest(BoardRequest request, Duration maxWait) throws ConnectorException {
        return map(c -> c.makeRequest(request, maxWait));
    }

    @Override
    public BoardResponse getResponse(UUID requestId, Duration maxWait) throws ConnectorException {
        return map(c -> c.getResponse(requestId, maxWait));
    }

    @Override
    public UUID getUUID() throws ConnectorException {
        return map(Connector::getUUID);
    }

    @Override
    public UUID provisionUuid() throws ConnectorException {
        return map(Connector::provisionUuid);
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
