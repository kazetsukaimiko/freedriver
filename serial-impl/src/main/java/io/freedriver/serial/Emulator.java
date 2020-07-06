package io.freedriver.serial;

import io.freedriver.serial.exception.SerialResourceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Emulator<T> implements SerialResource, Iterator<String>, Iterable<String> {
    private boolean opened = true;
    private final Function<T, String> stringFunction;
    private final List<T> values;
    private Iterator<T> valueIterator;
    private Iterator<String> lineIterator;
    private String nextCharacter = null;

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
    public Iterator<String> iterator() {
        return new Emulator<>(stringFunction, new ArrayList<>(values));
    }

    @Override
    public boolean hasNext() {
        return opened && (valueIterator.hasNext() || lineIterator.hasNext());
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new SerialResourceException("Emulator closed.", new RuntimeException("Emulator closed"));
        }
        if (lineIterator != null && lineIterator.hasNext()) {
            nextCharacter = lineIterator.next();
        } else {
            if (valueIterator.hasNext()) {
                lineIterator = new StringIterator(stringFunction.apply(valueIterator.next()));
                nextCharacter = "\n";
            } else {
                nextCharacter = "\n";
                opened = false;
            }
        }
        return nextCharacter;
    }

    public byte nextByte() {
        return 0;
    }



}
