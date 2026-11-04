package io.freedriver.metrics;

/**
 * Which {@link MetricsEmitter} method produced a {@link MetricsPoint}.
 */
public enum MetricsKind {
    INCREMENT,
    COUNT,
    GAUGE
}
