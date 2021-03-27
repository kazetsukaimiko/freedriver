package io.freedriver.jsonlink.jackson.schema.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.freedriver.jsonlink.jackson.AnalogPinNumberKeyDeserializer;
import io.freedriver.jsonlink.jackson.AnalogPinNumberSerializer;

@JsonSerialize(using = AnalogPinNumberSerializer.class)
@JsonDeserialize(keyUsing = AnalogPinNumberKeyDeserializer.class)
public class AnalogIdentifier extends Identifier {
    public AnalogIdentifier(int pin) {
        super(pin);
    }

    @Override
    public String toString() {
        return "A"+super.toString();
    }

    public static AnalogIdentifier of(String pin) {
        return new AnalogIdentifier(Integer.parseInt(pin.substring(1)));
    }
}
