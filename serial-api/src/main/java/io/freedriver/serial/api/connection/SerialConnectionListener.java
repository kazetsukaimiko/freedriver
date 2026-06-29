package io.freedriver.serial.api.connection;

import java.nio.file.Path;

/**
 * SPI for serial connection state transitions. Implementations receive events when a
 * {@link SerialConnectionState} changes (connect, disconnect, reconnect).
 */
@FunctionalInterface
public interface SerialConnectionListener {
    void onStateChanged(
            SerialDeviceIdentity identity,
            SerialConnectionState previous,
            SerialConnectionState current,
            Path resolvedPort);
}