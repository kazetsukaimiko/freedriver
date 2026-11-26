package io.freedriver.jsonlink.api.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The level of a digital pin. On the wire a state is a JSON boolean: {@code true} is
 * {@link #LOW} and {@code false} is {@link #HIGH}.
 */
public enum DigitalState {
    HIGH(false),
    LOW(true);

    private final boolean value;

    DigitalState(boolean value) {
        this.value = value;
    }

    /** The JSON boolean written for this state. */
    @JsonValue
    public boolean getValue() {
        return value;
    }

    /** Reads the JSON boolean back: the inverse of {@link #getValue()}. */
    @JsonCreator
    public static DigitalState fromBoolean(boolean value) {
        return value ? LOW : HIGH;
    }

    @Override
    public String toString() {
        return name();
    }
}
