package io.freedriver.serial.api.exception;

public class SerialStreamException extends RuntimeException {
    public SerialStreamException(String message) {
        super(message);
    }

    public SerialStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
