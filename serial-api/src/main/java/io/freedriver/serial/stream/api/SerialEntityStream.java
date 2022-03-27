package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.SerialResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SerialEntityStream<R> implements Iterator<R>, AutoCloseable {
    private final SerialInputStream inputStream;
    private final SerialOutputStream outputStream;

    private final Accumulator<ByteArrayOutputStream, R> accumulator;

    private boolean closed = false;

    private SerialEntityStream(SerialInputStream inputStream, SerialOutputStream outputStream, Accumulator<ByteArrayOutputStream, R> accumulator) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.accumulator = accumulator;
    }

    public SerialEntityStream(SerialResource resource, Accumulator<ByteArrayOutputStream, R> accumulator) {
        this(new SerialInputStream(resource), new SerialOutputStream(resource), accumulator);
    }

    public void write(int i) throws IOException {
        outputStream.write(i);
    }

    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws Exception {
        try {
            inputStream.close();
            outputStream.close();
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
