package io.freedriver.serial.api;

import java.nio.file.Path;

import io.freedriver.serial.api.params.SerialParams;

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
        return SerialResourceFactory.Holder.create(path, params);
    }
}
