package io.freedriver.jsonlink.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.freedriver.jsonlink.jackson.schema.v1.DigitalState;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import io.freedriver.jsonlink.jackson.schema.v1.Mode;

public class JsonLinkModule extends SimpleModule {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JsonLinkModule());

    public JsonLinkModule() {
        addKeyDeserializer(Identifier.class, new PinNumberKeyDeserializer());
        addSerializer(Mode.class, new ModeSerializer());
        addSerializer(DigitalState.class, new DigitalStateSerializer());
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
