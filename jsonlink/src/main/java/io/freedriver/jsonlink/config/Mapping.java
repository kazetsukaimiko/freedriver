package io.freedriver.jsonlink.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Mapping {
    private UUID connectorId;
    private String connectorName;
    private Map<Identifier, String> pinNames = new LinkedHashMap<>();

    public Mapping() {
    }

    public UUID getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(UUID connectorId) {
        this.connectorId = connectorId;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public Map<Identifier, String> getPinNames() {
        return pinNames;
    }

    @JsonIgnore
    public Map<String, Identifier> getNamedPins() {
        return pinNames.keySet()
                .stream()
                .collect(Collectors.toMap(
                        k -> pinNames.get(k),
                        k -> k,
                        (a, b) -> a
                ));
    }

    public void setPinNames(Map<Identifier, String> pinNames) {
        this.pinNames = pinNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mapping mapping = (Mapping) o;
        return Objects.equals(connectorId, mapping.connectorId) &&
                Objects.equals(pinNames, mapping.pinNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId);
    }

    @JsonIgnore
    public Set<PinName> getPinNamesAsEntities() {
        return getPinNames().entrySet()
                .stream()
                .map(PinName::fromEntry)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "connectorId=" + connectorId +
                ", connectorName='" + connectorName + '\'' +
                ", pinNames=" + pinNames +
                ", pinNamesAsEntities=" + getPinNamesAsEntities() +
                '}';
    }


}
