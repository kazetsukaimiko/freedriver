package io.freedriver.jsonlink.config;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import lombok.Builder;

@Builder(toBuilder = true)
public record PinName(Identifier pinNumber, String pinName) {
    @JsonIgnore
    public String group() {
        return Optional.ofNullable(pinName)
                .filter(s -> !s.isBlank())
                .map(s -> s.split("_")[0])
                .orElse(null);
    }

    @JsonIgnore
    public String unit() {
        return Optional.ofNullable(pinName)
                .filter(s -> !s.isBlank())
                .map(s -> s.split("_")[1])
                .orElse(null);
    }

    public static PinName fromEntry(Map.Entry<Identifier, String> identifierStringEntry) {
        return new PinName(identifierStringEntry.getKey(), identifierStringEntry.getValue());
    }

    public boolean ofGroup(String group) {
        return Objects.equals(group, group());
    }
}
