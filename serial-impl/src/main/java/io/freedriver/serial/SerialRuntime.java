package io.freedriver.serial;

import io.freedriver.serial.api.SerialResourceFactory;

public final class SerialRuntime {
    static {
        SerialResourceFactory.Holder.install(JSSCSerialResource::new);
    }

    private SerialRuntime() {
    }

    public static void ensureInstalled() {
        // Triggers the static initializer.
    }
}