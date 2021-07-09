package io.freedriver.serial;

import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.params.SerialParams;
import io.freedriver.serial.stream.api.PortReference;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiFunction;

public class SerialPortResourceSupplier extends SerialResourceSupplier<PortReference> {
    private final PortReference port;
    private final SerialParams serialParams;

    public SerialPortResourceSupplier(PortReference port, SerialParams serialParams) {
        this.port = port;
        this.serialParams = serialParams;
    }

    public SerialPortResourceSupplier(PortReference port) {
        this(port, new SerialParams());
    }

    @Override
    protected PortReference input() {
        return port;
    }

    @Override
    protected SerialParams params() {
        return serialParams;
    }

    @Override
    protected BiFunction<PortReference, SerialParams, SerialResource> resourceFunction() {
        return JSSCSerialResource::new;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialPortResourceSupplier that = (SerialPortResourceSupplier) o;
        return Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port);
    }
}
