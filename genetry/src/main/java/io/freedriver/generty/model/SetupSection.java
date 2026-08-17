package io.freedriver.generty.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record SetupSection(
        String model,
        BigDecimal hardver,
        String softver,
        int genCFG,
        BigDecimal sysHZ,
        BigDecimal UVPa,
        BigDecimal UVPe,
        BigDecimal OVPa,
        BigDecimal OVPe,
        @JsonProperty("ChargeIn") @JsonAlias("chargeIn") List<Integer> chargeIn) {
}
