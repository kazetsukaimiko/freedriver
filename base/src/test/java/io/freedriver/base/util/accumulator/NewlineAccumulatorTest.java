package io.freedriver.base.util.accumulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NewlineAccumulatorTest extends ByteArrayAccumulatorsTest<String, NewlineAccumulator> {
    @Override
    protected Accumulator<ByteArrayOutputStream, String> accumulator() {
        return NewlineAccumulator.INSTANCE;
    }

    @Override
    protected void write(String item) throws IOException {
        outputStream.write((item + '\n').getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected String random() {
        return UUID.randomUUID().toString();
    }
}
