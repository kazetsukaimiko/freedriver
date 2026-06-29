package io.freedriver.serial.api;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import io.freedriver.serial.api.params.SerialParams;

public abstract class SerialResourceSupplier<X> implements Supplier<SerialResource> {
    protected abstract X input();
    protected abstract SerialParams params();
    protected abstract BiFunction<X, SerialParams, SerialResource> resourceFunction();

    public final SerialResource get() {
        return resourceFunction().apply(input(), params());
    }
}
