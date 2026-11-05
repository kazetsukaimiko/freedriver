package io.freedriver.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Delegates to another emitter and adds {@code instanceId} to every call's tags.
 */
public final class InstancedMetricsEmitter implements MetricsEmitter {
    public static final String INSTANCE_ID_TAG = "instanceId";

    private final String instanceId;
    private final MetricsEmitter delegate;

    public InstancedMetricsEmitter(String instanceId, MetricsEmitter delegate) {
        if (instanceId == null || instanceId.isBlank()) {
            throw new IllegalArgumentException("instanceId");
        }
        this.instanceId = instanceId;
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public void increment(String key, Map<String, Object> tags) {
        delegate.increment(key, withInstanceId(tags));
    }

    @Override
    public void count(String key, Map<String, Object> tags) {
        delegate.count(key, withInstanceId(tags));
    }

    @Override
    public void gauge(String key, Map<String, Object> tags) {
        delegate.gauge(key, withInstanceId(tags));
    }

    public String instanceId() {
        return instanceId;
    }

    private Map<String, Object> withInstanceId(Map<String, Object> tags) {
        Map<String, Object> copy = new HashMap<>();
        if (tags != null) {
            copy.putAll(tags);
        }
        copy.put(INSTANCE_ID_TAG, instanceId);
        return Map.copyOf(copy);
    }
}
