package io.freedriver.serial.api;

import io.freedriver.serial.api.params.SerialParams;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class SerialResourceSupplier<X> implements Supplier<SerialResource> {
    protected abstract X input();
    protected abstract SerialParams params();
    protected abstract BiFunction<X, SerialParams, SerialResource> resourceFunction();

    public final SerialResource get() {
        return resourceFunction().apply(input(), params());
    }
}
