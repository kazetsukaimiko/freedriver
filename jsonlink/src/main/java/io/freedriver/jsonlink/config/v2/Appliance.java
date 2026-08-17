package io.freedriver.jsonlink.config.v2;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import lombok.Builder;

@Builder(toBuilder = true)
public record Appliance(Identifier identifier, String name, Set<String> groups) {
    public Appliance {
        groups = groups == null ? Set.of() : Set.copyOf(groups);
    }

    public Appliance(Identifier identifier, String name) {
        this(identifier, name, groupName(name).map(Set::of).orElseGet(Set::of));
    }

    private static Optional<String> groupName(String name) {
        if (name != null && name.split("_").length == 2) {
            return Optional.of(name.split("_")[0]);
        }
        return Optional.empty();
    }

    public io.freedriver.jsonlink.config.v3.Appliance migrate(UUID connectorId) {
        return io.freedriver.jsonlink.config.v3.Appliance.builder()
                .connectorId(connectorId)
                .groups(groups)
                .identifier(identifier)
                .build();
    }
}
