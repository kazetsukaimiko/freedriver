package io.freedriver.jsonlink.api.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An analog pin on a board. Its JSON form, as a value and as a map key, is the text
 * {@code "A"} followed by the pin number, for example {@code "A3"}.
 *
 * @param pin the analog pin number
 */
public record AnalogIdentifier(int pin) {

    /** Reads an analog identifier from its text form, {@code "A"} followed by the pin number. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AnalogIdentifier of(String pin) {
        return new AnalogIdentifier(Integer.parseInt(pin.substring(1)));
    }

    /** The text form, {@code "A"} followed by the pin number. */
    @JsonValue
    @Override
    public String toString() {
        return "A" + pin;
    }
}
