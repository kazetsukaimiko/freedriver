package io.freedriver.math.json.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.freedriver.math.measurement.types.Measurement;
import io.freedriver.math.number.ScaledNumber;

import java.io.IOException;
import java.math.BigDecimal;

public abstract class MeasurementDeserializer<M extends Measurement<M>> extends JsonDeserializer<M> {
    protected abstract M read(ScaledNumber s);

    @Override
    public M deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return read(ScaledNumber.of(jsonParser.readValueAs(BigDecimal.class)));
    }
}
