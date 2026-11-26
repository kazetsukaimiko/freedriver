package io.freedriver.jsonlink.api.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A digital pin on a board. As a JSON value it is the pin number; as a JSON map key it is
 * that number's decimal digits.
 *
 * @param pin the pin number, also the JSON form of this identifier
 */
public record Identifier(@JsonValue int pin) {

    /** Reads an identifier from its JSON number. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Identifier of(int pin) {
        return new Identifier(pin);
    }

    /** Reads an identifier from the decimal digits used for JSON map keys. */
    public static Identifier valueOf(String pin) {
        return new Identifier(Integer.parseInt(pin));
    }

    @Override
    public String toString() {
        return String.valueOf(pin);
    }

    public DigitalWrite setDigital(DigitalState state) {
        return new DigitalWrite(this, state);
    }

    public DigitalWrite setDigital(boolean state) {
        return new DigitalWrite(this, state);
    }

    public AnalogRead getAnalog(float voltage, float resistance) {
        return new AnalogRead(this, voltage, resistance);
    }

    public ModeSet setMode(Mode mode) {
        return new ModeSet(this, mode);
    }
}
