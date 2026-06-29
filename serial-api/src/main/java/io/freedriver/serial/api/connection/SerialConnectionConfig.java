package io.freedriver.serial.api.connection;

import java.time.Duration;

public final class SerialConnectionConfig {
    private final Duration monitorInterval;
    private final Duration reconnectBackoff;
    private final Duration openDelay;
    private final boolean indefiniteReadRetry;

    public SerialConnectionConfig(
            Duration monitorInterval,
            Duration reconnectBackoff,
            Duration openDelay,
            boolean indefiniteReadRetry) {
        this.monitorInterval = monitorInterval;
        this.reconnectBackoff = reconnectBackoff;
        this.openDelay = openDelay;
        this.indefiniteReadRetry = indefiniteReadRetry;
    }

    public static SerialConnectionConfig defaults() {
        return new SerialConnectionConfig(
                Duration.ofSeconds(2),
                Duration.ofMillis(500),
                Duration.ofMillis(200),
                true);
    }

    public Duration monitorInterval() {
        return monitorInterval;
    }

    public Duration reconnectBackoff() {
        return reconnectBackoff;
    }

    public Duration openDelay() {
        return openDelay;
    }

    public boolean indefiniteReadRetry() {
        return indefiniteReadRetry;
    }

    public SerialConnectionConfig withMonitorInterval(Duration monitorInterval) {
        return new SerialConnectionConfig(monitorInterval, reconnectBackoff, openDelay, indefiniteReadRetry);
    }
}