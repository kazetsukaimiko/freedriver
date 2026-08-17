package io.freedriver.jsonlink.jackson.schema.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.freedriver.jsonlink.jackson.AnalogPinNumberKeyDeserializer;
import io.freedriver.jsonlink.jackson.AnalogPinNumberSerializer;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonSerialize(using = AnalogPinNumberSerializer.class)
@JsonDeserialize(keyUsing = AnalogPinNumberKeyDeserializer.class)
public record AnalogIdentifier(int pin) {
    @Override
    public String toString() {
        return "A" + pin;
    }

    public static AnalogIdentifier of(String pin) {
        return new AnalogIdentifier(Integer.parseInt(pin.substring(1)));
    }
}
