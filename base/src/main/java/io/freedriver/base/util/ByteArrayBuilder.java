package io.freedriver.base.util;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Boilerplate replacement for StringBuilder.
 */
public class ByteArrayBuilder {
    private static final int BASE_SIZE = 1024;
    private byte[] underlying;
    private int position;

    public ByteArrayBuilder() {
        this.underlying = new byte[BASE_SIZE];
        this.position = 0;
    }

    public ByteArrayBuilder append(byte... input) {
        for (byte b : input) {
            if (position == underlying.length) {
                underlying = copy(underlying, underlying.length + BASE_SIZE);
            }
            underlying[position++] = b;
        }
        return this;
    }

    public int size() {
        return underlying.length;
    }

    public int position() {
        return position;
    }

    public byte[] build() {
        return copy(underlying, position);
    }

    public byte[] build(int start, int end) {
        return Arrays.copyOfRange(underlying, start, end);
    }

    public byte[] build(int newSize) {
        return build(0, newSize);
    }

    public byte lastByte() {
        return position == 0 ? underlying[position] : underlying[position-1];
    }

    public byte[] last(int size) {
        int start = Math.max(position-1, 0);
        return Arrays.copyOfRange(
                underlying,
                start,
                Math.min(start + size, size()-1));
    }

    private static byte[] copy(byte[] input, int newSize) {
        return (newSize > 0)
            ? Arrays.copyOf(input, newSize)
            : new byte[0];
    }
}
