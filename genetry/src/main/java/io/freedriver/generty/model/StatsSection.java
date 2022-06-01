package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsSection {
    private BigDecimal KWh;

    public BigDecimal getKWh() {
        return KWh;
    }

    public void setKWh(BigDecimal KWh) {
        this.KWh = KWh;
    }
}
