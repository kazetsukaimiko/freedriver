package io.freedriver.jsonlink.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;

public class PinNumberDeserializer extends JsonDeserializer<Identifier> {
    @Override
    public Identifier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Identifier.of(p.getValueAsInt());
    }
}
