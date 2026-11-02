package io.freedriver.metrics;

import java.util.Map;

/**
 * Shared emission surface for autonomy and freedriver-web.
 *
 * <p>This artifact ships mqtt, queueing, noop/logger, and instanced emitters. Grafana stays in
 * freedriver-web.
 */
public interface MetricsEmitter {
    void increment(String key, Map<String, Object> tags);

    void count(String key, Map<String, Object> tags);

    void gauge(String key, Map<String, Object> tags);
}
