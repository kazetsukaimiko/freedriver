package io.freedriver.serial;

import io.freedriver.serial.exception.SerialResourceTimeoutException;
import io.freedriver.serial.params.SerialParams;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Iterator;

public interface SerialResource extends Iterable<String>, Iterator<String>, AutoCloseable {

    /**
     * Clears the serial input buffer.
     */
    void clear();
    void write(byte[] array);
    String getName();
    boolean isOpened();
    byte nextByte();

    default String nextLine(Duration duration) throws SerialResourceTimeoutException {
        return next("\n", duration);
    }

    default String next(String delimiter, Duration timeout) throws SerialResourceTimeoutException {
        long start = System.currentTimeMillis();
        StringBuilder buffer = new StringBuilder();
        while (System.currentTimeMillis() < start + timeout.toMillis()) {
            String character = next();
            System.out.println("CHARACTER: " + character);
            buffer.append(character);
            String result = buffer.toString();
            if (result.endsWith(delimiter)) {
                return result;
            }
        }
        throw new SerialResourceTimeoutException("Timed out retrieving packet");
    }

    static SerialResource of(final Path path, SerialParams params) {
        return new JSSCSerialResource(path, params);
    }
}
