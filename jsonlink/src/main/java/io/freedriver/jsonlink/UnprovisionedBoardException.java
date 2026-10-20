package io.freedriver.jsonlink;

/** A board answered a UUID lookup and had no UUID. Lookup does not mint one. */
public class UnprovisionedBoardException extends ConnectorException {

    public UnprovisionedBoardException(String message) {
        super(message);
    }
}
