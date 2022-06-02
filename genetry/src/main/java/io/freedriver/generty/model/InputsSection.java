package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InputsSection {
    private BigDecimal inV;
    private BigDecimal inA;
    private BigDecimal xfA;
    private BigDecimal battV;

    public BigDecimal getInV() {
        return inV;
    }

    public void setInV(BigDecimal inV) {
        this.inV = inV;
    }

    public BigDecimal getInA() {
        return inA;
    }

    public void setInA(BigDecimal inA) {
        this.inA = inA;
    }

    public BigDecimal getXfA() {
        return xfA;
    }

    public void setXfA(BigDecimal xfA) {
        this.xfA = xfA;
    }

    public BigDecimal getBattV() {
        return battV;
    }

    @JsonSetter("battV")
    public void setBattV(BigDecimal battV) {
        this.battV = battV;
    }

    @JsonSetter("BattV")
    public void setOldBattV(BigDecimal BattV) {
        setBattV(BattV);
    }
}
