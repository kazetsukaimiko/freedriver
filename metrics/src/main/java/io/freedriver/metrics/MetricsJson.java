package io.freedriver.metrics;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson codec for {@link MetricsPoint} MQTT payloads.
 */
public final class MetricsJson {
    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    private MetricsJson() {}

    public static byte[] bytes(MetricsPoint point) {
        try {
            return MAPPER.writeValueAsBytes(point);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static MetricsPoint read(byte[] payload) {
        try {
            return MAPPER.readValue(payload, MetricsPoint.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
