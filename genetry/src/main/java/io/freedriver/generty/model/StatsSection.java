package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsSection {
    @JsonProperty("KWh")
    private BigDecimal KWh;

    public BigDecimal getKWh() {
        return KWh;
    }

    public void setKWh(BigDecimal KWh) {
        this.KWh = KWh;
    }
}
