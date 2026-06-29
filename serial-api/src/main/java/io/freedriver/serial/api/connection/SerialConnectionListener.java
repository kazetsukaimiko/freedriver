package io.freedriver.serial.api.connection;

import java.nio.file.Path;

@FunctionalInterface
public interface SerialConnectionListener {
    void onStateChanged(
            SerialDeviceIdentity identity,
            SerialConnectionState previous,
            SerialConnectionState current,
            Path resolvedPort);
}