package io.freedriver.serial.exception;

/**
 * Unchecked Exception to make SerialListenerExceptions a little easier to deal with.
 */
public class SerialResourceException extends RuntimeException {
    public SerialResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialResourceException(String message) {
        super(message);
    }
}
