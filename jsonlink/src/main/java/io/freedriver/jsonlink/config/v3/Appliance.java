package io.freedriver.jsonlink.config.v3;

import java.util.Set;
import java.util.UUID;

import io.freedriver.jsonlink.api.v1.Identifier;
import lombok.Builder;

@Builder(toBuilder = true)
public record Appliance(UUID connectorId, Identifier identifier, Set<String> groups) {
    public Appliance {
        groups = groups == null ? Set.of() : Set.copyOf(groups);
    }
}
