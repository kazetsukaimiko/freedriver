package io.freedriver.jsonlink.config.v2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import io.freedriver.jsonlink.jackson.schema.v1.Identifier;

public class Appliance {
    private Identifier identifier;
    private String name;
    private Set<String> groups = new HashSet<>();

    public Appliance(Identifier identifier, String name, Set<String> groups) {
        this.identifier = identifier;
        this.name = name;
        this.groups = groups;
    }

    public Appliance(Identifier identifier, String name) {
        this(identifier, name, groupName(name).map(Collections::singleton).orElseGet(Collections::emptySet));
    }

    public Appliance() {
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public io.freedriver.jsonlink.config.v3.Appliance migrate(UUID connectorId) {
        io.freedriver.jsonlink.config.v3.Appliance v3Appliance = new io.freedriver.jsonlink.config.v3.Appliance();
        v3Appliance.setConnectorId(connectorId);
        v3Appliance.setGroups(getGroups());
        v3Appliance.setIdentifier(getIdentifier());
        return v3Appliance;
    }
}
