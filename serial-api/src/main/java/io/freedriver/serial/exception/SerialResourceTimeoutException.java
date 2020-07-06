package io.freedriver.serial.exception;

public class SerialResourceTimeoutException extends SerialResourceException {
    public SerialResourceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialResourceTimeoutException(String message) {
        super(message);
    }
}
