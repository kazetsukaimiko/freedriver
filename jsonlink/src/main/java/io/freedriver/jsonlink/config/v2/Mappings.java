package io.freedriver.jsonlink.config.v2;

import io.freedriver.jsonlink.config.ConfigFile;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Deprecated
public class Mappings extends ConfigFile {
    private static final int DEFAULT_TTL_DAYS = 7;

    private Integer eventTTL = DEFAULT_TTL_DAYS;
    private ChronoUnit eventTTLUnit = ChronoUnit.DAYS;
    private Set<Mapping> mappings = new HashSet<>();

    public Mappings() {
    }

    public Integer getEventTTL() {
        return eventTTL != null
                ? eventTTL
                : DEFAULT_TTL_DAYS;
    }

    public void setEventTTL(Integer eventTTL) {
        this.eventTTL = eventTTL;
    }

    public ChronoUnit getEventTTLUnit() {
        return eventTTLUnit;
    }

    public void setEventTTLUnit(ChronoUnit eventTTLUnit) {
        this.eventTTLUnit = eventTTLUnit;
    }

    public Set<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(Set<Mapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public String toString() {
        return "Mappings{" +
                "mappings=" + mappings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mappings mappings1 = (Mappings) o;
        return Objects.equals(mappings, mappings1.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mappings);
    }

    @Override
    public String getFileName() {
        return "mappings_v2.json";
    }
}
