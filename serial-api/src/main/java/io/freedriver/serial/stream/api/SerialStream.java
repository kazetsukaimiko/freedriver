package io.freedriver.serial.stream.api;

import io.freedriver.base.util.ByteArrayBuilder;
import io.freedriver.serial.api.AutoCloseableWithState;
import io.freedriver.serial.api.exception.SerialResourceTimeoutException;
import io.freedriver.serial.api.exception.SerialStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * A much lighter weight Serial API compromised of:
 * An InputStream for reads
 * An OutputStream for writes
 */
public interface SerialStream extends AutoCloseableWithState {
    InputStream getInputStream();
    OutputStream getOutputStream();
    String getName();

    /*
     * TRANSITIONAL METHODS
     */
    default char next() {
        return (char) nextByte();
    }

    default byte nextByte() {
        try {
            return getInputStream().readNBytes(1)[0];
        } catch (IOException e) {
            throw new SerialStreamException("Unable to get next byte", e);
        }
    }

    default int clearTo(char c) {
        int read = 1;
        char t = next();
        while (c != t) {
            t = next();
            read++;
        }
        return read;
    }

    default String nextLine(Duration duration) throws SerialResourceTimeoutException {
        return readTo('\n', duration);
    }

    default byte[] readFor(Predicate<Byte> startPredicate, BiPredicate<Byte, ByteArrayBuilder> untilPredicate) {
        ByteArrayBuilder builder = new ByteArrayBuilder();
        byte read = nextByte();
        while (!startPredicate.test(read)) {
            read = nextByte();
        }
        builder.append(read);
        while (!untilPredicate.test(read, builder)) {
            read = nextByte();
            builder.append(read);
        }
        return builder.build();
    }

    default String readTo(char delimiter, Duration timeout) throws SerialResourceTimeoutException {
        long start = System.currentTimeMillis();
        StringBuilder buffer = new StringBuilder();
        while (System.currentTimeMillis() < start + timeout.toMillis()) {
            char character = next();
            buffer.append(character);
            if (Objects.equals(delimiter, character)) {
                return buffer.toString();
            }
        }
        throw new SerialResourceTimeoutException("Timed out retrieving packet, got '"+buffer.toString()+"'");
    }
}
