package io.freedriver.jsonlink.config;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import lombok.Builder;

@Builder(toBuilder = true)
public record Mapping(UUID connectorId, String connectorName, Map<Identifier, String> pinNames) {
    public Mapping {
        pinNames = pinNames == null ? Map.of() : Map.copyOf(pinNames);
    }

    @JsonIgnore
    public Map<String, Identifier> namedPins() {
        return pinNames.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue,
                        Map.Entry::getKey,
                        (a, b) -> a
                ));
    }

    @JsonIgnore
    public Set<PinName> pinNamesAsEntities() {
        return pinNames.entrySet().stream()
                .map(PinName::fromEntry)
                .collect(Collectors.toSet());
    }
}
