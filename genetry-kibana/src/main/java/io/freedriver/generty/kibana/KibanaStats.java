package io.freedriver.generty.kibana;

import io.freedriver.generty.model.FansSection;
import io.freedriver.generty.model.OutputsSection;
import io.freedriver.generty.model.Statsjson;
import io.freedriver.generty.model.TempsSection;

import java.math.BigDecimal;

/**
 * A Flattened version of stats.json to make it easier for kibana to visualize.
 */
public class KibanaStats {
    private String inverterId;
    private BigDecimal outV;
    private BigDecimal outA;
    private BigDecimal outW;
    private BigDecimal outPF;
    private BigDecimal outHZ;
    private BigDecimal xfEFF;

    private String rdx;
    private BigDecimal TTA;
    private BigDecimal TMA;
    private BigDecimal TTB;
    private BigDecimal TMB;

    private BigDecimal FA;
    private BigDecimal FB;
    private BigDecimal FC;

    public KibanaStats(String inverterId, BigDecimal outV, BigDecimal outA, BigDecimal outW, BigDecimal outPF, BigDecimal outHZ, BigDecimal xfEFF, String rdx, BigDecimal TTA, BigDecimal TMA, BigDecimal TTB, BigDecimal TMB, BigDecimal FA, BigDecimal FB, BigDecimal FC) {
        this.inverterId = inverterId;
        this.outV = outV;
        this.outA = outA;
        this.outW = outW;
        this.outPF = outPF;
        this.outHZ = outHZ;
        this.xfEFF = xfEFF;
        this.rdx = rdx;
        this.TTA = TTA;
        this.TMA = TMA;
        this.TTB = TTB;
        this.TMB = TMB;
        this.FA = FA;
        this.FB = FB;
        this.FC = FC;
    }

    public KibanaStats() {
    }

    public KibanaStats(String inverterId, OutputsSection outputs, TempsSection temps, FansSection fans) {
        this(
                inverterId,

                outputs.getOutV(),
                outputs.getOutA(),
                outputs.getOutW(),
                outputs.getOutPF(),
                outputs.getOutHZ(),
                outputs.getXfEFF(),

                temps.getRdx(),
                temps.getTTA(),
                temps.getTMA(),
                temps.getTTB(),
                temps.getTMB(),

                fans.getFA(),
                fans.getFB(),
                fans.getFC()
        );
    }

    public KibanaStats(String inverterId, Statsjson statsjson) {
        this(inverterId, statsjson.getOutputs(), statsjson.getTemps(), statsjson.getFans());
    }

    public String getInverterId() {
        return inverterId;
    }

    public void setInverterId(String inverterId) {
        this.inverterId = inverterId;
    }

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

    public BigDecimal getFA() {
        return FA;
    }

    public void setFA(BigDecimal FA) {
        this.FA = FA;
    }

    public BigDecimal getFB() {
        return FB;
    }

    public void setFB(BigDecimal FB) {
        this.FB = FB;
    }

    public BigDecimal getFC() {
        return FC;
    }

    public void setFC(BigDecimal FC) {
        this.FC = FC;
    }
}
