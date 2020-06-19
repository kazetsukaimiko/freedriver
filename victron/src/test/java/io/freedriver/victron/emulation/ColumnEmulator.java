package io.freedriver.victron.emulation;

import io.freedriver.serial.Emulator;
import io.freedriver.victron.VEDirectColumnValue;

import java.util.List;
import java.util.stream.Stream;

public class ColumnEmulator extends Emulator<VEDirectColumnValue> {
    public ColumnEmulator(List<VEDirectColumnValue> values) {
        super(VEDirectColumnValue::toSerialLine, values);
    }

    public ColumnEmulator(Stream<VEDirectColumnValue> valueStream) {
        super(VEDirectColumnValue::toSerialLine, valueStream);
    }

    public ColumnEmulator(VEDirectColumnValue single) {
        super(VEDirectColumnValue::toSerialLine, single);
    }
    //implements SerialResource, Iterator<String>, Iterable<String> {
    /*
    private boolean opened = true;
    private List<VEDirectColumnValue> values;
    private Iterator<VEDirectColumnValue> valueIterator;
    private Iterator<String> lineIterator;
    private String nextCharacter = null;

    public ColumnEmulator(List<VEDirectColumnValue> values) {
        this.values = values;
        valueIterator = values.iterator();
    }

    public ColumnEmulator(Stream<VEDirectColumnValue> valueStream) {
        this(valueStream.collect(Collectors.toList()));
    }

    public ColumnEmulator(VEDirectColumnValue single) {
        this(Collections.singletonList(single));
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
        return new ColumnEmulator(new ArrayList<>(values));
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
                lineIterator = new StringIterator(valueIterator.next().toSerialLine());
                nextCharacter = "\n";
            } else {
                nextCharacter = "\n";
                opened = false;
            }
        }
        return nextCharacter;
    }
*/


}
