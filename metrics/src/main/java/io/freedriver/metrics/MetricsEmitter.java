package io.freedriver.metrics;

import java.util.Map;

/**
 * Shared emission surface for autonomy and freedriver-web. Implementations live
 * in later cards (mqtt, queueing, noop/logger, instanced, Grafana).
 */
public interface MetricsEmitter {
    void increment(String key, Map<String, Object> tags);

    void count(String key, Map<String, Object> tags);

    void gauge(String key, Map<String, Object> tags);
}
