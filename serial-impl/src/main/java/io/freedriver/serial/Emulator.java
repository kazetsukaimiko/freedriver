package io.freedriver.serial;

import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.exception.SerialResourceException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Emulator<T> implements SerialResource<T>, Iterator<T>, Iterable<T> {
    private boolean opened = true;
    private final Function<T, String> stringFunction;
    private final List<T> values;
    private Iterator<T> valueIterator;
    private Iterator<T> lineIterator;
    private Character nextCharacter = null;

    public Emulator(Function<T, String> stringFunction, List<T> values) {
        this.stringFunction = stringFunction;
        this.values = values;
        valueIterator = values.iterator();
    }

    public Emulator(Function<T, String> stringFunction, Stream<T> valueStream) {
        this(stringFunction, valueStream.collect(Collectors.toList()));
    }

    public Emulator(Function<T, String> stringFunction, T single) {
        this(stringFunction,
                Collections.singletonList(single));
    }

    @Override
    public boolean isClosed() {
        return !opened;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return "Emulator";
    }

    @Override
    public void close() throws Exception {
        opened = false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Emulator<>(stringFunction, new ArrayList<>(values));
    }

    @Override
    public boolean hasNext() {
        return opened && (valueIterator.hasNext() || lineIterator.hasNext());
    }

    @Override
    public T next() {
        return lineIterator.next();
    }

    public byte nextByte() {
        return 0;
    }

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public Duration getPoll() {
        return Duration.ofMillis(5);
    }


}
