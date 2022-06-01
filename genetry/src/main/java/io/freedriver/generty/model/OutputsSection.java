package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputsSection {
    private BigDecimal outV;
    private BigDecimal outA;
    private BigDecimal outW;
    private BigDecimal outPF;
    private BigDecimal outHZ;
    private BigDecimal xfEFF;

    public BigDecimal getOutV() {
        return outV;
    }

    public void setOutV(BigDecimal outV) {
        this.outV = outV;
    }

    public BigDecimal getOutA() {
        return outA;
    }

    public void setOutA(BigDecimal outA) {
        this.outA = outA;
    }

    public BigDecimal getOutW() {
        return outW;
    }

    public void setOutW(BigDecimal outW) {
        this.outW = outW;
    }

    public BigDecimal getOutPF() {
        return outPF;
    }

    public void setOutPF(BigDecimal outPF) {
        this.outPF = outPF;
    }

    public BigDecimal getOutHZ() {
        return outHZ;
    }

    public void setOutHZ(BigDecimal outHZ) {
        this.outHZ = outHZ;
    }

    public BigDecimal getXfEFF() {
        return xfEFF;
    }

    public void setXfEFF(BigDecimal xfEFF) {
        this.xfEFF = xfEFF;
    }
}
