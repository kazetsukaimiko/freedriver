package io.freedriver.serial.api;

import io.freedriver.serial.api.params.SerialParams;

import java.nio.file.Path;

public interface SerialResource extends AutoCloseable {

    void write(byte[] array);
    byte[] read(int size);

    /**
     * Clears the serial input buffer.
     */
    void clear();

    String getName();
    boolean isOpened();

    static SerialResource of(final Path path, SerialParams params) {
        throw new UnsupportedOperationException("Need to find implementations");
        //return new JSSCSerialResource(path, params);
    }
}
