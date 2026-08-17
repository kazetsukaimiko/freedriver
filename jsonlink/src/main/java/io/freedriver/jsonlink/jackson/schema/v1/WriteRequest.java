package io.freedriver.jsonlink.jackson.schema.v1;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Builder;

@Builder(toBuilder = true)
public record WriteRequest(Map<Identifier, DigitalState> digital) {
    public WriteRequest {
        digital = digital == null ? Map.of() : Map.copyOf(digital);
    }

    public static WriteRequest empty() {
        return new WriteRequest(Map.of());
    }

    public WriteRequest writeDigital(DigitalWrite pinWrite) {
        Map<Identifier, DigitalState> next = new LinkedHashMap<>(digital);
        next.put(pinWrite.pinNumber(), pinWrite.operation());
        return toBuilder().digital(next).build();
    }

    public boolean isEmpty() {
        return digital.isEmpty();
    }
}
