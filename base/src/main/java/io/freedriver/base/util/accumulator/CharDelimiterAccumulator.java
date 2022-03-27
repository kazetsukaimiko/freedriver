package io.freedriver.base.util.accumulator;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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
            if (array.length > 0) {
                return array[array.length - 1] == delimiter;
            }
            return false;
        } catch (Error error) {
            // Probably not a real problem, like the byte array not being long enough.
            LOGGER.log(Level.WARNING, String.format("Warning: Cannot decode byte array with charset %s: ", charset), error);
            return false;
        }
    }

    @Override
    public String convert(ByteArrayOutputStream input) {
        CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(input.toByteArray()));
        return charBuffer.limit(charBuffer.length()-1).toString();
    }
}
