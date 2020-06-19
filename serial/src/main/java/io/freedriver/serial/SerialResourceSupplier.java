package io.freedriver.serial;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SerialResourceSupplier<X> implements Supplier<SerialResource> {
    protected abstract X input();
    protected abstract Function<X, SerialResource> resourceFunction();

    public final SerialResource get() {
        return resourceFunction().apply(input());
    }

}
