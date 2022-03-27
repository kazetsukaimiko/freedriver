package io.freedriver.base.util;

import io.freedriver.base.util.accumulator.Accumulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EntityStreamWithOutput<R> extends EntityStream<R> {
    private final OutputStream outputStream;

    public EntityStreamWithOutput(InputStream inputStream, OutputStream outputStream, Accumulator<ByteArrayOutputStream, R> accumulator) {
        super(inputStream, accumulator);
        this.outputStream = outputStream;
    }

    public void write(int i) throws IOException {
        outputStream.write(i);
    }

    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    @Override
    public void close() throws Exception {
        try {
            outputStream.close();
        } finally {
            super.close();
        }
    }
}
