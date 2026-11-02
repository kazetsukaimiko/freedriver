package io.freedriver.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class InstancedMetricsEmitterTest {
    private static final String INSTANCE = UUID.fromString("550e8400-e29b-41d4-a716-446655440000").toString();

    @Test
    void addsInstanceIdToEveryCall() {
        RecordingMetricsEmitter recording = new RecordingMetricsEmitter();
        InstancedMetricsEmitter emitter = new InstancedMetricsEmitter(INSTANCE, recording);

        emitter.increment("io.calls", Map.of("n", 1));
        emitter.count("io.bytes", Map.of());
        emitter.gauge("io.queue", null);

        assertEquals(3, recording.points.size());
        assertEquals(INSTANCE, recording.points.get(0).tags().get("instanceId"));
        assertEquals(1, ((Number) recording.points.get(0).tags().get("n")).intValue());
        assertEquals(INSTANCE, recording.points.get(1).tags().get("instanceId"));
        assertEquals(INSTANCE, recording.points.get(2).tags().get("instanceId"));
        assertEquals(
                List.of(MetricsKind.INCREMENT, MetricsKind.COUNT, MetricsKind.GAUGE),
                kinds(recording.points));
    }

    @Test
    void instanceIdWinsOverCallerTagAndDoesNotMutateCallerMap() {
        RecordingMetricsEmitter recording = new RecordingMetricsEmitter();
        InstancedMetricsEmitter emitter = new InstancedMetricsEmitter(INSTANCE, recording);
        Map<String, Object> tags = new HashMap<>();
        tags.put("instanceId", "other");
        tags.put("n", 1);

        emitter.count("io.bytes", tags);

        assertEquals("other", tags.get("instanceId"));
        assertEquals(INSTANCE, recording.points.get(0).tags().get("instanceId"));
        assertThrows(UnsupportedOperationException.class, () -> recording.points.get(0).tags().put("x", 1));
    }

    @Test
    void blankInstanceIdIsRejected() {
        RecordingMetricsEmitter recording = new RecordingMetricsEmitter();
        assertThrows(IllegalArgumentException.class, () -> new InstancedMetricsEmitter(" ", recording));
        assertThrows(NullPointerException.class, () -> new InstancedMetricsEmitter(INSTANCE, null));
    }

    @Test
    void wrapsQueueingMqttSoFailedPublishRetriesWithInstanceId() {
        List<MetricsPoint> published = new ArrayList<>();
        MqttMetricsEmitter.Publisher flaky = new MqttMetricsEmitter.Publisher() {
            private int remaining = 1;

            @Override
            public void publish(String topic, byte[] payload, int qos, boolean retain) {
                if (remaining > 0) {
                    remaining--;
                    throw new IllegalStateException("broker down");
                }
                published.add(MetricsJson.read(payload));
            }
        };
        QueueingMetricsEmitter emitter = new QueueingMetricsEmitter(
                new InstancedMetricsEmitter(
                        INSTANCE, new MqttMetricsEmitter(MqttMetricsEmitter.topic(INSTANCE), flaky)));

        emitter.increment("io.calls", Map.of("n", 1));
        assertEquals(1, emitter.queued());
        emitter.flush();

        assertEquals(0, emitter.queued());
        assertEquals(1, published.size());
        assertEquals(INSTANCE, published.get(0).tags().get("instanceId"));
        assertEquals("io.calls", published.get(0).key());
        assertEquals(MetricsKind.INCREMENT, published.get(0).kind());
    }

    private static List<MetricsKind> kinds(List<MetricsPoint> points) {
        List<MetricsKind> kinds = new ArrayList<>();
        for (MetricsPoint point : points) {
            kinds.add(point.kind());
        }
        return kinds;
    }
}
