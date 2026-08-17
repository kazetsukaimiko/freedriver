package io.freedriver.jsonlink.config.v2;

import java.time.temporal.ChronoUnit;
import java.util.Set;

import io.freedriver.jsonlink.config.ConfigFile;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Deprecated
@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
@Jacksonized
public class Mappings extends ConfigFile {
    private static final int DEFAULT_TTL_DAYS = 7;

    private final Integer eventTTL;
    private final ChronoUnit eventTTLUnit;
    private final Set<Mapping> mappings;

    public Mappings(Integer eventTTL, ChronoUnit eventTTLUnit, Set<Mapping> mappings) {
        this.eventTTL = eventTTL != null ? eventTTL : DEFAULT_TTL_DAYS;
        this.eventTTLUnit = eventTTLUnit != null ? eventTTLUnit : ChronoUnit.DAYS;
        this.mappings = mappings == null ? Set.of() : Set.copyOf(mappings);
    }

    @Override
    public String getFileName() {
        return "mappings_v2.json";
    }
}
