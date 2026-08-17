package io.freedriver.jsonlink.jackson.schema.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.freedriver.jsonlink.jackson.PinNumberDeserializer;
import io.freedriver.jsonlink.jackson.PinNumberKeyDeserializer;
import io.freedriver.jsonlink.jackson.PinNumberSerializer;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonSerialize(using = PinNumberSerializer.class)
@JsonDeserialize(using = PinNumberDeserializer.class, keyUsing = PinNumberKeyDeserializer.class)
public record Identifier(int pin) {
    public static Identifier of(int pin) {
        return new Identifier(pin);
    }

    @Override
    public String toString() {
        return String.valueOf(pin);
    }

    public DigitalWrite setDigital(DigitalState b) {
        return new DigitalWrite(this, b);
    }

    public DigitalWrite setDigital(boolean b) {
        return new DigitalWrite(this, b);
    }

    public AnalogRead getAnalog(float voltage, float resistance) {
        return new AnalogRead(this, voltage, resistance);
    }

    public ModeSet setMode(Mode mode) {
        return new ModeSet(this, mode);
    }
}
