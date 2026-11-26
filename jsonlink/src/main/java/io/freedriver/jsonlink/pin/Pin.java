package io.freedriver.jsonlink.pin;

import io.freedriver.jsonlink.Connector;
import io.freedriver.jsonlink.ConnectorException;
import io.freedriver.jsonlink.api.v1.BoardRequest;
import io.freedriver.jsonlink.api.v1.Identifier;
import io.freedriver.jsonlink.api.v1.Mode;

/**
 * Class designed to perform atomic pin operations.
 */
public abstract class Pin {
    private final Connector connector;
    private final Identifier identifier;
    private final Mode mode;

    public Pin(Connector connector, Identifier identifier, Mode mode) throws ConnectorException {
        this.connector = connector;
        this.identifier = identifier;
        this.mode = mode;
        connector.send(BoardRequest.empty().modeSet(identifier.setMode(mode)));
    }

    public Connector getConnector() {
        return connector;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Mode getMode() {
        return mode;
    }
}
