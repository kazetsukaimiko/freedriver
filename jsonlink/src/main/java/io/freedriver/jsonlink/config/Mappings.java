package io.freedriver.jsonlink.config;

import java.util.Set;
import java.util.stream.Collectors;

import io.freedriver.jsonlink.config.v2.Appliance;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
@Jacksonized
public class Mappings extends ConfigFile implements Migration<io.freedriver.jsonlink.config.v2.Mappings> {
    private final Set<Mapping> mappings;

    public Mappings(Set<Mapping> mappings) {
        this.mappings = mappings == null ? Set.of() : Set.copyOf(mappings);
    }

    @Override
    public io.freedriver.jsonlink.config.v2.Mappings migrate() {
        return io.freedriver.jsonlink.config.v2.Mappings.builder()
                .mappings(mappings.stream()
                        .map(this::migrateMapping)
                        .collect(Collectors.toSet()))
                .build();
    }

    public io.freedriver.jsonlink.config.v2.Mapping migrateMapping(Mapping config) {
        return io.freedriver.jsonlink.config.v2.Mapping.builder()
                .connectorId(config.connectorId())
                .connectorName(config.connectorName())
                .appliances(config.pinNames().entrySet().stream()
                        .map(e -> new Appliance(e.getKey(), e.getValue()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public String getFileName() {
        return "mappings.json";
    }
}
