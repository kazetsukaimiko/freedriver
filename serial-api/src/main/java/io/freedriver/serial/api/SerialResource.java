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

    /**
     * Not implemented in serial-api. Use {@link SerialResourceFactory.Holder#create(Path, SerialParams)}
     * with serial-impl on the classpath, or construct a {@code SerialResource} implementation directly.
     */
    static SerialResource of(final Path path, SerialParams params) {
        throw new UnsupportedOperationException(
                "Use SerialResourceFactory.Holder.create(path, params); requires serial-impl on the classpath");
    }
}
