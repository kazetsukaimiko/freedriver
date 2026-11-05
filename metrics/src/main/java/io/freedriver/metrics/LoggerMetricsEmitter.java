package io.freedriver.metrics;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs every call. Does not publish.
 */
public final class LoggerMetricsEmitter implements MetricsEmitter {
    private final Logger logger;
    private final Level level;

    public LoggerMetricsEmitter() {
        this(Logger.getLogger(LoggerMetricsEmitter.class.getName()), Level.INFO);
    }

    public LoggerMetricsEmitter(Logger logger, Level level) {
        this.logger = Objects.requireNonNull(logger, "logger");
        this.level = Objects.requireNonNull(level, "level");
    }

    @Override
    public void increment(String key, Map<String, Object> tags) {
        log(MetricsKind.INCREMENT, key, tags);
    }

    @Override
    public void count(String key, Map<String, Object> tags) {
        log(MetricsKind.COUNT, key, tags);
    }

    @Override
    public void gauge(String key, Map<String, Object> tags) {
        log(MetricsKind.GAUGE, key, tags);
    }

    private void log(MetricsKind kind, String key, Map<String, Object> tags) {
        if (!logger.isLoggable(level)) {
            return;
        }
        logger.log(
                level,
                "metrics " + kind.name().toLowerCase() + " key=" + oneLine(key) + " tags=" + oneLine(tags));
    }

    private static String oneLine(Object value) {
        if (value == null) {
            return "-";
        }
        return String.valueOf(value).replace('\r', ' ').replace('\n', ' ');
    }
}
