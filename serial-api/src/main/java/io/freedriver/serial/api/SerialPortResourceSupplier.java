package io.freedriver.serial.api;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiFunction;

import io.freedriver.serial.api.params.SerialParams;

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
        return SerialResourceFactory.Holder::create;
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
