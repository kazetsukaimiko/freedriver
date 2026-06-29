package io.freedriver.serial.api.connection;

import java.nio.file.Path;
import java.util.Optional;

import io.freedriver.serial.api.SerialResource;

/**
 * A managed connection to one serial device. The {@link #resource()} reference is stable across reconnects.
 */
public interface SerialConnectionHandle extends AutoCloseable {
    SerialDeviceIdentity identity();

    SerialConnectionState state();

    Optional<Path> resolvedPort();

    SerialResource resource();

    void addListener(SerialConnectionListener listener);

    void removeListener(SerialConnectionListener listener);

    @Override
    void close();
}