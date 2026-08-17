package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record Statsjson(
        SetupSection setup,
        OutputsSection outputs,
        InputsSection inputs,
        StatsSection stats,
        TempsSection temps,
        FansSection fans,
        ErrorsSection errors) {
}
