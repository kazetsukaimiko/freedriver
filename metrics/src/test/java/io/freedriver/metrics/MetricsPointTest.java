package io.freedriver.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

class MetricsPointTest {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void incrementCountAndGaugeShareKeyAndTags() {
        Map<String, Object> tags = Map.of("instanceId", "house-1", "value", 3);
        RecordingEmitter emitter = new RecordingEmitter();

        emitter.increment("io.calls", tags);
        emitter.count("io.bytes", tags);
        emitter.gauge("io.queue", tags);

        assertEquals(
                List.of(MetricsKind.INCREMENT, MetricsKind.COUNT, MetricsKind.GAUGE),
                emitter.kinds);
        assertEquals("io.calls", emitter.points.get(0).key());
        assertEquals(tags, emitter.points.get(1).tags());
        assertEquals(MetricsKind.GAUGE, emitter.points.get(2).kind());
    }

    @Test
    void tagsAreCopied() {
        Map<String, Object> tags = new HashMap<>();
        tags.put("k", "v");
        MetricsPoint point = MetricsPoint.increment("n", tags);
        tags.put("k", "changed");

        assertEquals(Map.of("k", "v"), point.tags());
        assertThrows(UnsupportedOperationException.class, () -> point.tags().put("x", 1));
    }

    @Test
    void blankKeyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> MetricsPoint.increment(" ", Map.of()));
        assertThrows(NullPointerException.class, () -> new MetricsPoint(null, MetricsKind.COUNT, Map.of(), Instant.now()));
    }

    @Test
    void jacksonRoundTripKeepsKindAndTags() throws Exception {
        Instant at = Instant.parse("2026-11-02T01:00:00Z");
        MetricsPoint original = new MetricsPoint(
                "io.calls",
                MetricsKind.COUNT,
                Map.of("instanceId", "house-1", "value", 4),
                at);

        MetricsPoint read = mapper.readValue(mapper.writeValueAsString(original), MetricsPoint.class);

        assertEquals(original.key(), read.key());
        assertEquals(original.kind(), read.kind());
        assertEquals(original.timestamp(), read.timestamp());
        assertEquals("house-1", read.tags().get("instanceId"));
        assertEquals(4, ((Number) read.tags().get("value")).intValue());
        assertTrue(read.tags().containsKey("value"));
    }

    private static final class RecordingEmitter implements MetricsEmitter {
        private final List<MetricsPoint> points = new ArrayList<>();
        private final List<MetricsKind> kinds = new ArrayList<>();

        @Override
        public void increment(String key, Map<String, Object> tags) {
            record(MetricsPoint.increment(key, tags));
        }

        @Override
        public void count(String key, Map<String, Object> tags) {
            record(MetricsPoint.count(key, tags));
        }

        @Override
        public void gauge(String key, Map<String, Object> tags) {
            record(MetricsPoint.gauge(key, tags));
        }

        private void record(MetricsPoint point) {
            points.add(point);
            kinds.add(point.kind());
        }
    }
}
