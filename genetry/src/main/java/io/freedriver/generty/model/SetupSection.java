package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SetupSection {
    private String model;
    private BigDecimal hardver;
    private String softver;
    private int genCFG;
    private BigDecimal sysHZ;
    private BigDecimal UVPa;
    private BigDecimal UVPe;
    private BigDecimal OVPa;
    private BigDecimal OVPe;
    private List<Integer> ChargeIn;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getHardver() {
        return hardver;
    }

    public void setHardver(BigDecimal hardver) {
        this.hardver = hardver;
    }

    public String getSoftver() {
        return softver;
    }

    public void setSoftver(String softver) {
        this.softver = softver;
    }

    public int getGenCFG() {
        return genCFG;
    }

    public void setGenCFG(int genCFG) {
        this.genCFG = genCFG;
    }

    public BigDecimal getSysHZ() {
        return sysHZ;
    }

    public void setSysHZ(BigDecimal sysHZ) {
        this.sysHZ = sysHZ;
    }

    public BigDecimal getUVPa() {
        return UVPa;
    }

    public void setUVPa(BigDecimal UVPa) {
        this.UVPa = UVPa;
    }

    public BigDecimal getUVPe() {
        return UVPe;
    }

    public void setUVPe(BigDecimal UVPe) {
        this.UVPe = UVPe;
    }

    public BigDecimal getOVPa() {
        return OVPa;
    }

    public void setOVPa(BigDecimal OVPa) {
        this.OVPa = OVPa;
    }

    public BigDecimal getOVPe() {
        return OVPe;
    }

    public void setOVPe(BigDecimal OVPe) {
        this.OVPe = OVPe;
    }

    public List<Integer> getChargeIn() {
        return ChargeIn;
    }

    public void setChargeIn(List<Integer> chargeIn) {
        ChargeIn = chargeIn;
    }
}
