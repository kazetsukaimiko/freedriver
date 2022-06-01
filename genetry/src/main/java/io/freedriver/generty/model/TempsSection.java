package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TempsSection {
    private String rdx;

    @JsonProperty("TTA")
    private BigDecimal TTA;
    @JsonProperty("TMA")
    private BigDecimal TMA;
    @JsonProperty("TTB")
    private BigDecimal TTB;
    @JsonProperty("TMB")
    private BigDecimal TMB;

    public String getRdx() {
        return rdx;
    }

    public void setRdx(String rdx) {
        this.rdx = rdx;
    }

    public BigDecimal getTTA() {
        return TTA;
    }

    public void setTTA(BigDecimal TTA) {
        this.TTA = TTA;
    }

    public BigDecimal getTMA() {
        return TMA;
    }

    public void setTMA(BigDecimal TMA) {
        this.TMA = TMA;
    }

    public BigDecimal getTTB() {
        return TTB;
    }

    public void setTTB(BigDecimal TTB) {
        this.TTB = TTB;
    }

    public BigDecimal getTMB() {
        return TMB;
    }

    public void setTMB(BigDecimal TMB) {
        this.TMB = TMB;
    }


}
