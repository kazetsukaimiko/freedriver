package io.freedriver.metrics;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MQTT metrics payload. Emitters serialize this body; this module does not publish it.
 */
public record MetricsPoint(
        @JsonProperty(required = true) String key,
        @JsonProperty(required = true) MetricsKind kind,
        Map<String, Object> tags,
        Instant timestamp) {

    public MetricsPoint {
        Objects.requireNonNull(key, "key");
        if (key.isBlank()) {
            throw new IllegalArgumentException("key");
        }
        Objects.requireNonNull(kind, "kind");
        tags = tags == null ? Map.of() : Map.copyOf(tags);
        timestamp = timestamp == null ? Instant.now() : timestamp;
    }

    public static MetricsPoint increment(String key, Map<String, Object> tags) {
        return new MetricsPoint(key, MetricsKind.INCREMENT, tags, Instant.now());
    }

    public static MetricsPoint count(String key, Map<String, Object> tags) {
        return new MetricsPoint(key, MetricsKind.COUNT, tags, Instant.now());
    }

    public static MetricsPoint gauge(String key, Map<String, Object> tags) {
        return new MetricsPoint(key, MetricsKind.GAUGE, tags, Instant.now());
    }
}
