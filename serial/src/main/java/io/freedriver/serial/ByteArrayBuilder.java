package io.freedriver.serial;

/**
 * Boilerplate- replacement for StringBuilder.
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

    public byte[] build() {
        return copy(underlying, position);
    }

    private static byte[] copy(byte[] input, int newSize) {
        byte[] newArray = new byte[newSize];
        if (newSize>0) {
            for (int i = 0; i < input.length && i < newSize; i++) {
                newArray[i] =
                        input[i];
            }
        }
        return newArray;
    }
}
