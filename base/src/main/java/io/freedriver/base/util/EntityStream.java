package io.freedriver.base.util;

import io.freedriver.base.util.accumulator.Accumulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EntityStream<R> implements Iterator<R>, AutoCloseable {
    private final InputStream inputStream;

    private final Accumulator<ByteArrayOutputStream, R> accumulator;

    private boolean closed = false;

    protected EntityStream(InputStream inputStream, Accumulator<ByteArrayOutputStream, R> accumulator) {
        this.inputStream = inputStream;
        this.accumulator = accumulator;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws Exception {
        try {
            inputStream.close();
        } finally {
            closed = true;
        }
    }

    public Stream<R> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
    }

    @Override
    public boolean hasNext() {
        return !closed;
    }

    @Override
    public R next() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (!accumulator.isComplete(baos)) {
            try {
                baos.write(inputStream.read());
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
        }
        return accumulator.convert(baos);
    }
}
