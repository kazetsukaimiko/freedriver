package io.freedriver.serial.api.connection;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SerialConnectionListenerTest {
    @Test
    void discoversRegisteredProviders() {
        assertTrue(
                SerialConnectionListener.load().anyMatch(TestSerialConnectionListener.class::isInstance),
                "expected TestSerialConnectionListener from META-INF/services");
    }
}