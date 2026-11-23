package io.freedriver.jsonlink.api.v1;

/** One pin and the state to write to it. */
public record DigitalWrite(Identifier pinNumber, DigitalState operation) {
    public DigitalWrite(Identifier pinNumber, boolean operation) {
        this(pinNumber, DigitalState.fromBoolean(operation));
    }
}
