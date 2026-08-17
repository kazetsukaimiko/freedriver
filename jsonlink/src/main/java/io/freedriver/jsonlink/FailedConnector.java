package io.freedriver.jsonlink;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record FailedConnector(String device, Instant instant) {
    private static final Duration DEFAULT_DURATION = Duration.of(5, MINUTES);
    private static final Duration TIMEOUT_DURATION = Duration.of(30, SECONDS);

    public static FailedConnector failed(String device) {
        return new FailedConnector(device, Instant.now().plus(DEFAULT_DURATION));
    }

    public static FailedConnector timedOut(String device) {
        return new FailedConnector(device, Instant.now().plus(TIMEOUT_DURATION));
    }

    public boolean failureExpired() {
        return Instant.now().isAfter(instant);
    }

    public Duration delay() {
        return Duration.between(Instant.now(), instant).abs();
    }
}
