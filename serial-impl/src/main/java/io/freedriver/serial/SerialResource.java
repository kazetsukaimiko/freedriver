package io.freedriver.serial;

import io.freedriver.serial.api.exception.SerialResourceTimeoutException;
import io.freedriver.serial.api.params.SerialParams;

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

    default int clearToNewline() {
        int read = 0;
        String rest = "";
        while (!rest.endsWith("\n")) {
            rest = nextLine(Duration.ofMillis(5));
            read = read + rest.length();
        }
        return read;
    }

    default String nextLine(Duration duration) throws SerialResourceTimeoutException {
        return next("\n", duration);
    }

    default String next(String delimiter, Duration timeout) throws SerialResourceTimeoutException {
        long start = System.currentTimeMillis();
        StringBuilder buffer = new StringBuilder();
        while (System.currentTimeMillis() < start + timeout.toMillis()) {
            String character = next();
            buffer.append(character);
            String result = buffer.toString();
            if (result.endsWith(delimiter)) {
                if (System.currentTimeMillis() - start > 100) {

                }
                return result;
            }
        }
        throw new SerialResourceTimeoutException("Timed out retrieving packet, got '"+buffer.toString()+"'");
    }

    static SerialResource of(final Path path, SerialParams params) {
        return new JSSCSerialResource(path, params);
    }
}
