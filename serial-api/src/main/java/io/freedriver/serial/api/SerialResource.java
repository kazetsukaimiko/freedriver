package io.freedriver.serial.api;

import io.freedriver.serial.api.exception.SerialResourceTimeoutException;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Iterator;
import java.util.Objects;

public interface SerialResource<T> extends Iterable<T>, Iterator<T>, AutoCloseableWithState {

    /**
     * Clears the serial input buffer.
     */
    void clear();
    void write(byte[] array);
    String getName();
    boolean isOpened();
    byte nextByte();
    Charset getCharset();
    Duration getPoll();

    default int clearTo(T c) {
        int read = 1;
        T t = next();
        while (!Objects.equals(t, c)) {
            t = next();
            read++;
        }
        return read;
    }

    default String nextLine(Duration duration) throws SerialResourceTimeoutException {
        return readTo('\n', duration);
    }

    default String readTo(char delimiter, Duration timeout) throws SerialResourceTimeoutException {
        long start = System.currentTimeMillis();
        StringBuilder buffer = new StringBuilder();
        while (System.currentTimeMillis() < start + timeout.toMillis()) {
            T character = next();
            buffer.append(character);
            if (Objects.equals(delimiter, character)) {
                return buffer.toString();
            }
        }
        throw new SerialResourceTimeoutException("Timed out retrieving packet, got '"+buffer.toString()+"'");
    }
}
