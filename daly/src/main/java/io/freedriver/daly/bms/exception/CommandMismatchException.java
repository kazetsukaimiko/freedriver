package io.freedriver.daly.bms.exception;

import io.freedriver.daly.bms.CommandId;

// I asked for one metric and got a different one in response
public class CommandMismatchException extends BmsException {
    public CommandMismatchException(CommandId expected, CommandId actual) {
        super("Expected commandId " + expected + ", got " + actual);
    }
}
