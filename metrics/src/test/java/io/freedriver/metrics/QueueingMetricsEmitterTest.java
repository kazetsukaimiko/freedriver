package io.freedriver.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class QueueingMetricsEmitterTest {

    @Test
    void queuesFailedCallAndResendsOnNextSuccess() {
        FlakyMetricsEmitter flaky = new FlakyMetricsEmitter(1);
        QueueingMetricsEmitter emitter = new QueueingMetricsEmitter(flaky);

        emitter.increment("io.calls", Map.of("n", 1));
        assertEquals(1, emitter.queued());
        assertEquals(List.of(), keys(flaky.delegate.points));

        emitter.count("io.bytes", Map.of("n", 2));

        assertEquals(0, emitter.queued());
        assertEquals(List.of("io.calls", "io.bytes"), keys(flaky.delegate.points));
        assertEquals(MetricsKind.INCREMENT, flaky.delegate.points.get(0).kind());
        assertEquals(MetricsKind.COUNT, flaky.delegate.points.get(1).kind());
    }

    @Test
    void flushResendsWithoutANewCall() {
        FlakyMetricsEmitter flaky = new FlakyMetricsEmitter(1);
        QueueingMetricsEmitter emitter = new QueueingMetricsEmitter(flaky);

        emitter.gauge("io.queue", Map.of("n", 3));
        assertEquals(1, emitter.queued());

        emitter.flush();

        assertEquals(0, emitter.queued());
        assertEquals("io.queue", flaky.delegate.points.get(0).key());
        assertEquals(MetricsKind.GAUGE, flaky.delegate.points.get(0).kind());
    }

    @Test
    void queuedTagsAreCopied() {
        FlakyMetricsEmitter flaky = new FlakyMetricsEmitter(1);
        QueueingMetricsEmitter emitter = new QueueingMetricsEmitter(flaky);
        Map<String, Object> tags = new HashMap<>();
        tags.put("k", "v");

        emitter.increment("io.calls", tags);
        tags.put("k", "changed");
        emitter.flush();

        assertEquals(Map.of("k", "v"), flaky.delegate.points.get(0).tags());
    }

    @Test
    void keepsQueueWhenResendStillFails() {
        FlakyMetricsEmitter flaky = new FlakyMetricsEmitter(4);
        QueueingMetricsEmitter emitter = new QueueingMetricsEmitter(flaky);

        emitter.increment("a", Map.of());
        emitter.count("b", Map.of());
        assertEquals(2, emitter.queued());

        emitter.flush();
        assertEquals(2, emitter.queued());
        assertEquals(List.of(), keys(flaky.delegate.points));
    }

    @Test
    void nullDelegateIsRejected() {
        assertThrows(NullPointerException.class, () -> new QueueingMetricsEmitter(null));
    }

    private static List<String> keys(List<MetricsPoint> points) {
        List<String> keys = new ArrayList<>();
        for (MetricsPoint point : points) {
            keys.add(point.key());
        }
        return keys;
    }

    private static final class FlakyMetricsEmitter implements MetricsEmitter {
        private final AtomicInteger remainingFailures;
        private final RecordingMetricsEmitter delegate = new RecordingMetricsEmitter();

        private FlakyMetricsEmitter(int failures) {
            this.remainingFailures = new AtomicInteger(failures);
        }

        @Override
        public void increment(String key, Map<String, Object> tags) {
            maybeFail();
            delegate.increment(key, tags);
        }

        @Override
        public void count(String key, Map<String, Object> tags) {
            maybeFail();
            delegate.count(key, tags);
        }

        @Override
        public void gauge(String key, Map<String, Object> tags) {
            maybeFail();
            delegate.gauge(key, tags);
        }

        private void maybeFail() {
            if (remainingFailures.get() > 0) {
                remainingFailures.decrementAndGet();
                throw new IllegalStateException("downstream failed");
            }
        }
    }
}
