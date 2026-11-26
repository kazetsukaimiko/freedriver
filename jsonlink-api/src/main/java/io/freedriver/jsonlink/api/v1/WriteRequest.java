package io.freedriver.jsonlink.api.v1;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** The digital writes of a {@link BoardRequest}, keyed by pin. */
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
        return new WriteRequest(next);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return digital.isEmpty();
    }
}
