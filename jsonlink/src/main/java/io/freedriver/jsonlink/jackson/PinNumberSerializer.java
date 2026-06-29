package io.freedriver.jsonlink.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;

public class PinNumberSerializer extends JsonSerializer<Identifier> {

    @Override
    public void serialize(Identifier pinNumber, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(pinNumber.getPin());
    }
}
