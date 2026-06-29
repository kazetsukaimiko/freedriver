package io.freedriver.serial.api;

/**
 * Low-level serial port I/O. Create instances via {@link SerialResourceFactory.Holder#create}
 * with {@code serial-impl} on the classpath.
 */
public interface SerialResource extends AutoCloseable {

    void write(byte[] array);
    byte[] read(int size);

    /**
     * Clears the serial input buffer.
     */
    void clear();

    String getName();
    boolean isOpened();
}