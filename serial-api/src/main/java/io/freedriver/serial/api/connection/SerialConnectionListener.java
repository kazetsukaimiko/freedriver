package io.freedriver.serial.api.connection;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.stream.Stream;

/**
 * SPI for serial connection state transitions. Implementations receive events when a
 * {@link SerialConnectionState} changes (connect, disconnect, reconnect).
 *
 * <p>Register providers via {@code META-INF/services/} and discover them with {@link #load()}.
 */
@FunctionalInterface
public interface SerialConnectionListener {
    void onStateChanged(
            SerialDeviceIdentity identity,
            SerialConnectionState previous,
            SerialConnectionState current,
            Path resolvedPort);

    /**
     * Discovers registered {@link SerialConnectionListener} providers.
     *
     * <p>Uses this interface's defining class loader, not the current thread context class loader.
     */
    static Stream<SerialConnectionListener> load() {
        return ServiceLoader.load(SerialConnectionListener.class, SerialConnectionListener.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get);
    }
}