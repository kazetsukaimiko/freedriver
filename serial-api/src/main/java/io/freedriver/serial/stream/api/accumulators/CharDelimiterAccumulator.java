package io.freedriver.serial.stream.api.accumulators;

import io.freedriver.serial.stream.api.Accumulator;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CharDelimiterAccumulator implements Accumulator<ByteArrayOutputStream, String> {
    private static final Logger LOGGER = Logger.getLogger(CharDelimiterAccumulator.class.getName());
    private final char delimiter;
    private final Charset charset;

    public CharDelimiterAccumulator(char delimiter, Charset charset) {
        this.delimiter = delimiter;
        this.charset = charset;
    }

    @Override
    public boolean isComplete(ByteArrayOutputStream input) {
        try {
            char[] array = charset.decode(ByteBuffer.wrap(input.toByteArray())).array();
            return array[array.length - 1] == delimiter;
        } catch (Error error) {
            // Probably not a real problem, like the byte array not being long enough.
            LOGGER.log(Level.WARNING, String.format("Warning: Cannot decode byte array with charset %s: ", charset), error);
            return false;
        }
    }

    @Override
    public String convert(ByteArrayOutputStream input) {
        return charset.decode(ByteBuffer.wrap(input.toByteArray())).toString();
    }
}
