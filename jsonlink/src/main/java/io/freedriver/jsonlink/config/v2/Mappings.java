package io.freedriver.jsonlink.config.v2;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.freedriver.jsonlink.config.ConfigFile;
import jakarta.validation.Valid;
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
    /**
     * Board id to the actions to run, in order, when that board enumerates.
     * Absent or null in the file loads as an empty map.
     */
    @JsonDeserialize(using = StartupActionsDeserializer.class)
    private final Map<String, List<@Valid MappingsAction>> startupActions;

    public Mappings(
            Integer eventTTL,
            ChronoUnit eventTTLUnit,
            Set<Mapping> mappings,
            Map<String, List<@Valid MappingsAction>> startupActions) {
        this.eventTTL = eventTTL != null ? eventTTL : DEFAULT_TTL_DAYS;
        this.eventTTLUnit = eventTTLUnit != null ? eventTTLUnit : ChronoUnit.DAYS;
        this.mappings = mappings == null ? Set.of() : Set.copyOf(mappings);
        this.startupActions = copyStartupActions(startupActions);
    }

    private static Map<String, List<MappingsAction>> copyStartupActions(
            Map<String, List<MappingsAction>> startupActions) {
        if (startupActions == null || startupActions.isEmpty()) {
            return Map.of();
        }
        Map<String, List<MappingsAction>> copy = new LinkedHashMap<>();
        startupActions.forEach((boardId, actions) ->
                copy.put(boardId, actions == null ? List.of() : List.copyOf(actions)));
        return Collections.unmodifiableMap(copy);
    }

    @Override
    public String getFileName() {
        return "mappings_v2.json";
    }
}
