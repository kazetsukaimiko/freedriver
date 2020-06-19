package io.freedriver.serial;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SerialReader {
    private final Supplier<SerialResource> resourceSupplier;

    public SerialReader(Supplier<SerialResource> resourceSupplier) {
        this.resourceSupplier = resourceSupplier;
    }

    /**
     * Opens a Stream from a VE Direct Serial device in a safe (auto-closing) way.
     * @param accumulator Is the algorithm for constructing instances of X.
     * @param <X> Is the type of object returned.
     * @return A Stream of {X} that filters nulls and automatically closes.
     */
    public <X> Stream<X> openStream(Function<SerialListener<X>, X> accumulator) {
        SerialListener<X> listener = new SerialListener<>(resourceSupplier.get(), accumulator);
        //readUntilChecksum(listener);
        return listener.stream()
                .filter(Objects::nonNull)
                .onClose(UncheckedCloseable.wrap(listener));
    }



    /**
     * Reads raw lines from the VE.Direct device.
     * @return A Stream of Strings, each String being a line from the VE.Direct connection.
     */
    public Stream<String> lineStream() {
        return openStream(SerialListener<String>::nextLine);
    }

    public Stream<byte[]> byteStream() {
        return openStream(SerialListener<byte[]>::nextLineByteArray);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialReader that = (SerialReader) o;
        return Objects.equals(resourceSupplier, that.resourceSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceSupplier);
    }
}
