package io.freedriver.jsonlink.jackson.schema.v1;

import lombok.Builder;

@Builder(toBuilder = true)
public record DigitalWrite(Identifier pinNumber, DigitalState operation) {
    public DigitalWrite(Identifier pinNumber, boolean operation) {
        this(pinNumber, DigitalState.fromBoolean(operation));
    }
}
