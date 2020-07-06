package io.freedriver.serial;

import io.freedriver.serial.params.SerialParams;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiFunction;

public class SerialPortResourceSupplier extends SerialResourceSupplier<Path> {
    private final Path path;
    private final SerialParams serialParams;

    public SerialPortResourceSupplier(Path path, SerialParams serialParams) {
        this.path = path;
        this.serialParams = serialParams;
    }

    public SerialPortResourceSupplier(Path path) {
        this(path, new SerialParams());
    }

    @Override
    protected Path input() {
        return path;
    }

    @Override
    protected SerialParams params() {
        return serialParams;
    }

    @Override
    protected BiFunction<Path, SerialParams, SerialResource> resourceFunction() {
        return SerialResource::of;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialPortResourceSupplier that = (SerialPortResourceSupplier) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
