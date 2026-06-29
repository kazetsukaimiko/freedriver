package io.freedriver.serial.api.connection;

import java.time.Duration;

public record SerialConnectionConfig(
        Duration monitorInterval,
        Duration reconnectBackoff,
        Duration openDelay,
        boolean indefiniteReadRetry) {

    public static SerialConnectionConfig defaults() {
        return new SerialConnectionConfig(
                Duration.ofSeconds(2),
                Duration.ofMillis(500),
                Duration.ofMillis(200),
                true);
    }

    public SerialConnectionConfig withMonitorInterval(Duration monitorInterval) {
        return new SerialConnectionConfig(monitorInterval, reconnectBackoff, openDelay, indefiniteReadRetry);
    }
}