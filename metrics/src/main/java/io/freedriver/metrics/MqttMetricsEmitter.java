package io.freedriver.metrics;

import java.util.Map;
import java.util.Objects;

/**
 * Publishes a {@link MetricsPoint} JSON body to the instance metrics MQTT topic.
 *
 * <p>The topic is {@code freedriver/v1/{instanceId}/metrics}. QoS 1, retain false. The publisher
 * is supplied by the host (autonomy or freedriver-web); this module does not open a broker
 * session.
 */
public final class MqttMetricsEmitter implements MetricsEmitter {
    public static final String TOPIC_TEMPLATE = "freedriver/v1/{instanceId}/metrics";
    public static final int QOS = 1;
    public static final boolean RETAIN = false;

    private final String topic;
    private final Publisher publisher;

    public MqttMetricsEmitter(String topic, Publisher publisher) {
        if (topic == null || topic.isBlank() || topic.contains("+") || topic.contains("#")) {
            throw new IllegalArgumentException("Exact metrics topic only");
        }
        this.topic = topic;
        this.publisher = Objects.requireNonNull(publisher, "publisher");
    }

    public static String topic(String instanceId) {
        if (instanceId == null
                || instanceId.isBlank()
                || instanceId.contains("/")
                || instanceId.contains("+")
                || instanceId.contains("#")) {
            throw new IllegalArgumentException("instanceId");
        }
        return "freedriver/v1/" + instanceId + "/metrics";
    }

    @Override
    public void increment(String key, Map<String, Object> tags) {
        publish(MetricsPoint.increment(key, tags));
    }

    @Override
    public void count(String key, Map<String, Object> tags) {
        publish(MetricsPoint.count(key, tags));
    }

    @Override
    public void gauge(String key, Map<String, Object> tags) {
        publish(MetricsPoint.gauge(key, tags));
    }

    public String topic() {
        return topic;
    }

    private void publish(MetricsPoint point) {
        publisher.publish(topic, MetricsJson.bytes(point), QOS, RETAIN);
    }

    @FunctionalInterface
    public interface Publisher {
        void publish(String topic, byte[] payload, int qos, boolean retain);
    }
}
