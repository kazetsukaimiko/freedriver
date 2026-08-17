package io.freedriver.generty.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record OutputsSection(
        BigDecimal outV,
        BigDecimal outA,
        BigDecimal outW,
        BigDecimal outPF,
        BigDecimal outHZ,
        BigDecimal xfEFF) {
}
