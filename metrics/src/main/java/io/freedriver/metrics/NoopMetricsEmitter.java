package io.freedriver.metrics;

import java.util.Map;

/**
 * Drops every call.
 */
public final class NoopMetricsEmitter implements MetricsEmitter {
    public static final NoopMetricsEmitter INSTANCE = new NoopMetricsEmitter();

    @Override
    public void increment(String key, Map<String, Object> tags) {}

    @Override
    public void count(String key, Map<String, Object> tags) {}

    @Override
    public void gauge(String key, Map<String, Object> tags) {}
}
