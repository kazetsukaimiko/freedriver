package io.freedriver.jsonlink.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.freedriver.jsonlink.jackson.schema.v1.DigitalState;

public class DigitalStateSerializer extends JsonSerializer<DigitalState> {
    @Override
    public void serialize(DigitalState digitalState, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeBoolean(digitalState.getValue());
    }
}
