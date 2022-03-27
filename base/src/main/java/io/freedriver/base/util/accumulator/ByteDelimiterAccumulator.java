package io.freedriver.base.util.accumulator;

import io.freedriver.base.util.ByteArrayBuilder;
import io.freedriver.base.util.ByteConverter;

public class ByteDelimiterAccumulator implements Accumulator<ByteArrayBuilder, String> {
    private final byte delimiter;

    public ByteDelimiterAccumulator(byte delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public boolean isComplete(ByteArrayBuilder input) {
        byte lastByte = input.lastByte();
        return (lastByte == delimiter);
    }

    @Override
    public String convert(ByteArrayBuilder input) {
        return ByteConverter.byteArrayToString(input.build(input.position()-1));
    }
}
