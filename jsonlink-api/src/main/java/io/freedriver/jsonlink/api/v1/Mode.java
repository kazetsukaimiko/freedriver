package io.freedriver.jsonlink.api.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The direction of a digital pin. On the wire a mode is a JSON boolean: {@code true} is
 * {@link #OUTPUT} and {@code false} is {@link #INPUT}.
 */
public enum Mode {
    INPUT(false),
    OUTPUT(true);

    private final boolean modeValue;

    Mode(boolean modeValue) {
        this.modeValue = modeValue;
    }

    /** The JSON boolean written for this mode. */
    @JsonValue
    public boolean getModeValue() {
        return modeValue;
    }

    /** Reads the JSON boolean back: the inverse of {@link #getModeValue()}. */
    @JsonCreator
    public static Mode ofModeValue(boolean modeValue) {
        return modeValue ? OUTPUT : INPUT;
    }
}
