package io.freedriver.metrics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class NoopAndLoggerMetricsEmitterTest {

    @Test
    void noopDropsCalls() {
        NoopMetricsEmitter emitter = NoopMetricsEmitter.INSTANCE;

        assertDoesNotThrow(() -> {
            emitter.increment("io.calls", Map.of("n", 1));
            emitter.count("io.bytes", null);
            emitter.gauge("io.queue", Map.of());
        });
    }

    @Test
    void loggerWritesKindKeyAndTagsOnOneLine() {
        Logger logger = Logger.getLogger("io.freedriver.metrics.test.logger");
        CapturingHandler handler = new CapturingHandler();
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        logger.addHandler(handler);
        try {
            LoggerMetricsEmitter emitter = new LoggerMetricsEmitter(logger, Level.INFO);
            emitter.increment("io.calls", Map.of("instanceId", "house-1"));
            emitter.count("io.bytes\nsecret", Map.of());
            emitter.gauge("io.queue", Map.of("n", 2));
        } finally {
            logger.removeHandler(handler);
        }

        String all = String.join("\n", handler.messages);
        assertTrue(all.contains("metrics increment key=io.calls tags={instanceId=house-1}"));
        assertTrue(all.contains("metrics count key=io.bytes secret tags="));
        assertTrue(all.contains("metrics gauge key=io.queue tags="));
        assertTrue(handler.messages.stream().noneMatch(line -> line.contains("\n")));
    }

    private static final class CapturingHandler extends Handler {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            messages.add(record.getMessage());
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
    }
}
