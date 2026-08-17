package io.freedriver.generty.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record InputsSection(
        BigDecimal inV,
        BigDecimal inA,
        BigDecimal xfA,
        @JsonAlias({"battV", "BattV"}) BigDecimal battV) {
}
