package io.freedriver.serial.api.connection;

import java.nio.file.Path;

public final class TestSerialConnectionListener implements SerialConnectionListener {
    @Override
    public void onStateChanged(
            SerialDeviceIdentity identity,
            SerialConnectionState previous,
            SerialConnectionState current,
            Path resolvedPort) {
        // test stub
    }
}