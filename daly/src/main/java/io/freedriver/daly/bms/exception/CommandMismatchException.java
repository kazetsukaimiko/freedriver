package io.freedriver.daly.bms.exception;

import io.freedriver.daly.bms.QueryId;

// I asked for one metric and got a different one in response
public class CommandMismatchException extends BmsException {
    public CommandMismatchException(QueryId expected, QueryId actual) {
        super("Expected commandId " + expected + ", got " + actual);
    }
}
