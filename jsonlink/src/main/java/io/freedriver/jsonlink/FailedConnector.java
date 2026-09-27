package io.freedriver.jsonlink;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record FailedConnector(String device, Instant instant) {
    private static final Duration DEFAULT_DURATION = Duration.of(5, MINUTES);
    private static final Duration TIMEOUT_DURATION = Duration.of(30, SECONDS);
    private static volatile Clock clock = Clock.systemUTC();

    /**
     * Clock for the backoff deadline. Tests advance this instead of waiting on
     * {@link Instant#now()}.
     */
    static void useClock(Clock clock) {
        FailedConnector.clock = clock == null ? Clock.systemUTC() : clock;
    }

    public static FailedConnector failed(String device) {
        return new FailedConnector(device, clock.instant().plus(DEFAULT_DURATION));
    }

    public static FailedConnector timedOut(String device) {
        return new FailedConnector(device, clock.instant().plus(TIMEOUT_DURATION));
    }

    public boolean failureExpired() {
        return clock.instant().isAfter(instant);
    }

    public Duration delay() {
        return Duration.between(clock.instant(), instant).abs();
    }
}
