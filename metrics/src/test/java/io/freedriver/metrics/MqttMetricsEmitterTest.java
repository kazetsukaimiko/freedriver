package io.freedriver.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class MqttMetricsEmitterTest {
    private static final String INSTANCE = UUID.fromString("550e8400-e29b-41d4-a716-446655440000").toString();

    @Test
    void topicFollowsInstanceMetricsPath() {
        String topic = MqttMetricsEmitter.topic(INSTANCE);

        assertEquals("freedriver/v1/" + INSTANCE + "/metrics", topic);
        assertEquals("freedriver/v1/{instanceId}/metrics", MqttMetricsEmitter.TOPIC_TEMPLATE);
        assertFalse(topic.contains("+"));
        assertFalse(topic.contains("#"));
        assertEquals(1, MqttMetricsEmitter.QOS);
        assertFalse(MqttMetricsEmitter.RETAIN);
    }

    @Test
    void publishesJsonPointToExactTopic() {
        RecordingPublisher publisher = new RecordingPublisher();
        MqttMetricsEmitter emitter = new MqttMetricsEmitter(MqttMetricsEmitter.topic(INSTANCE), publisher);

        emitter.increment("io.calls", Map.of("value", 1));
        emitter.count("io.bytes", Map.of("value", 4));
        emitter.gauge("io.queue", Map.of("value", 2));

        assertEquals(3, publisher.messages.size());
        assertEquals(MqttMetricsEmitter.topic(INSTANCE), publisher.messages.get(0).topic);
        assertEquals(1, publisher.messages.get(0).qos);
        assertFalse(publisher.messages.get(0).retain);

        MetricsPoint increment = MetricsJson.read(publisher.messages.get(0).payload);
        MetricsPoint count = MetricsJson.read(publisher.messages.get(1).payload);
        MetricsPoint gauge = MetricsJson.read(publisher.messages.get(2).payload);

        assertEquals("io.calls", increment.key());
        assertEquals(MetricsKind.INCREMENT, increment.kind());
        assertEquals(1, ((Number) increment.tags().get("value")).intValue());
        assertEquals(MetricsKind.COUNT, count.kind());
        assertEquals(MetricsKind.GAUGE, gauge.kind());
        assertEquals("io.queue", gauge.key());
    }

    @Test
    void rejectsWildcardTopicAndBlankInstanceId() {
        RecordingPublisher publisher = new RecordingPublisher();

        assertThrows(IllegalArgumentException.class, () -> new MqttMetricsEmitter("freedriver/v1/+/metrics", publisher));
        assertThrows(IllegalArgumentException.class, () -> new MqttMetricsEmitter("freedriver/v1/#", publisher));
        assertThrows(IllegalArgumentException.class, () -> MqttMetricsEmitter.topic(" "));
        assertThrows(IllegalArgumentException.class, () -> MqttMetricsEmitter.topic("a/b"));
        assertThrows(NullPointerException.class, () -> new MqttMetricsEmitter(MqttMetricsEmitter.topic(INSTANCE), null));
    }

    @Test
    void publisherFailurePropagates() {
        MqttMetricsEmitter emitter = new MqttMetricsEmitter(
                MqttMetricsEmitter.topic(INSTANCE),
                (topic, payload, qos, retain) -> {
                    throw new IllegalStateException("broker down");
                });

        assertThrows(IllegalStateException.class, () -> emitter.count("io.bytes", Map.of()));
    }

    private static final class RecordingPublisher implements MqttMetricsEmitter.Publisher {
        private final List<Published> messages = new ArrayList<>();

        @Override
        public void publish(String topic, byte[] payload, int qos, boolean retain) {
            messages.add(new Published(topic, payload, qos, retain));
        }
    }

    private record Published(String topic, byte[] payload, int qos, boolean retain) {}
}
