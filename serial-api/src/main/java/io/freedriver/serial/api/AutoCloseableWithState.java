package io.freedriver.serial.api;

public interface AutoCloseableWithState extends AutoCloseable {
    boolean isClosed();
    default boolean isOpened() {
        return !isClosed();
    }
}
