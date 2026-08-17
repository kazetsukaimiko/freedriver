package io.freedriver.generty.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record FansSection(
        @JsonProperty("FA") BigDecimal FA,
        @JsonProperty("FB") BigDecimal FB,
        @JsonProperty("FC") BigDecimal FC) {
}
