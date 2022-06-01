package io.freedriver.jsonlink.config.v3;

import io.freedriver.jsonlink.config.ConfigFile;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Appliance {
    private UUID connectorId;
    private Identifier identifier;
    private Set<String> groups = new HashSet<>();

    public Appliance(Identifier identifier, String name, Set<String> groups) {
        this.groups = groups;
    }

    public Appliance(Identifier identifier, String name) {
        this(identifier, name, groupName(name).map(Collections::singleton).orElseGet(Collections::emptySet));
    }

    public Appliance() {
    }

    public UUID getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(UUID connectorId) {
        this.connectorId = connectorId;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    private static Optional<String> groupName(String name) {
        if (name.split("_").length == 2) {
            return Optional.of(name.split("_")[0]);
        }
        return Optional.empty();
    }

}
