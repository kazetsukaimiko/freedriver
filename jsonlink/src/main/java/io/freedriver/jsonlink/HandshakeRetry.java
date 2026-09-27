package io.freedriver.jsonlink;

import java.time.Duration;
import java.util.logging.Logger;

/**
 * How many times to open the serial port and read the board UUID, and how long
 * to wait after each open before that handshake. Arduino boards auto-reset when
 * the port opens and typically need 1.5 to 2 seconds before they answer.
 *
 * <p>Defaults are {@value #DEFAULT_MAX_ATTEMPTS} attempts and a 2 second wait.
 * Override with system properties {@code jsonlink.handshake.maxAttempts} and
 * {@code jsonlink.handshake.retryDelay} ({@code PT2S} or a millisecond count).
 */
public record HandshakeRetry(int maxAttempts, Duration retryDelay) {
    public static final int DEFAULT_MAX_ATTEMPTS = 3;
    public static final Duration DEFAULT_RETRY_DELAY = Duration.ofSeconds(2);
    private static final Logger LOGGER = Logger.getLogger(HandshakeRetry.class.getName());

    public HandshakeRetry {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }
        if (retryDelay == null || retryDelay.isNegative()) {
            throw new IllegalArgumentException("retryDelay must be zero or positive");
        }
    }

    public static HandshakeRetry defaults() {
        int attempts = Integer.getInteger("jsonlink.handshake.maxAttempts", DEFAULT_MAX_ATTEMPTS);
        if (attempts < 1) {
            LOGGER.warning("jsonlink.handshake.maxAttempts must be at least 1; using " + DEFAULT_MAX_ATTEMPTS);
            attempts = DEFAULT_MAX_ATTEMPTS;
        }
        return new HandshakeRetry(attempts, parseDelay(System.getProperty("jsonlink.handshake.retryDelay")));
    }

    private static Duration parseDelay(String raw) {
        if (raw == null || raw.isBlank()) {
            return DEFAULT_RETRY_DELAY;
        }
        try {
            String trimmed = raw.trim();
            Duration parsed = trimmed.startsWith("P")
                    ? Duration.parse(trimmed)
                    : Duration.ofMillis(Long.parseLong(trimmed));
            if (parsed.isNegative()) {
                LOGGER.warning("jsonlink.handshake.retryDelay '" + raw + "' is negative; using " + DEFAULT_RETRY_DELAY);
                return DEFAULT_RETRY_DELAY;
            }
            return parsed;
        } catch (RuntimeException e) {
            LOGGER.warning("Invalid jsonlink.handshake.retryDelay '" + raw + "'; using " + DEFAULT_RETRY_DELAY);
            return DEFAULT_RETRY_DELAY;
        }
    }
}
