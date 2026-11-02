package io.freedriver.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class RecordingMetricsEmitter implements MetricsEmitter {
    final List<MetricsPoint> points = new ArrayList<>();

    @Override
    public void increment(String key, Map<String, Object> tags) {
        points.add(MetricsPoint.increment(key, tags));
    }

    @Override
    public void count(String key, Map<String, Object> tags) {
        points.add(MetricsPoint.count(key, tags));
    }

    @Override
    public void gauge(String key, Map<String, Object> tags) {
        points.add(MetricsPoint.gauge(key, tags));
    }
}
