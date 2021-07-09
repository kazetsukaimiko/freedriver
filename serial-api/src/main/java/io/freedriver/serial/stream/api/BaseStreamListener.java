package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.exception.SerialStreamException;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

public class BaseStreamListener<T> implements StreamListener<T> {
    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private final SerialStream stream;
    private final Accumulator<T> accumulator;
    private long countRead = 0L;

    public BaseStreamListener(SerialStream stream, Accumulator<T> accumulator) {
        this.stream = stream;
        this.accumulator = accumulator;
    }

    public SerialStream getStream() {
        return stream;
    }

    @Override
    public long countRead() {
        return countRead;
    }

    public Accumulator<T> getAccumulator() {
        return accumulator;
    }

    @Override
    public boolean isClosed() {
        return stream.isClosed();
    }

    @Override
    public void close() throws Exception {
        stream.close();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return !isClosed();
    }

    @Override
    public T next() {
        try {
            T item = accumulator.apply(stream);
            countRead++;
            return item;
        } catch (IOException e) {
            throw new SerialStreamException("Couldn't get next element: ", e);
        }
    }
}
