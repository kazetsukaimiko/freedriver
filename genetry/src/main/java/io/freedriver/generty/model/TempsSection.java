package io.freedriver.generty.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record TempsSection(
        String rdx,
        @JsonProperty("TTA") BigDecimal TTA,
        @JsonProperty("TMA") BigDecimal TMA,
        @JsonProperty("TTB") BigDecimal TTB,
        @JsonProperty("TMB") BigDecimal TMB) {
}
