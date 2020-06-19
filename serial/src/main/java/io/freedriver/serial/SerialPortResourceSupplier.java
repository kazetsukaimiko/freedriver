package io.freedriver.serial;

import jssc.SerialPort;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

public class SerialPortResourceSupplier extends SerialResourceSupplier<Path> {
    private final Path path;
    private final int baudRate;

    public SerialPortResourceSupplier(Path path, int baudRate) {
        this.path = path;
        this.baudRate = baudRate;
    }

    public SerialPortResourceSupplier(Path path) {
        this(path, SerialPort.BAUDRATE_19200);
    }

    @Override
    protected Path input() {
        return path;
    }

    @Override
    protected Function<Path, SerialResource> resourceFunction() {
        return p -> SerialResource.of(new SerialPort(p.toAbsolutePath().toString()), baudRate);
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
