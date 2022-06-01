package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FansSection {

    @JsonProperty("FA")
    private BigDecimal FA;
    @JsonProperty("FB")
    private BigDecimal FB;
    @JsonProperty("FC")
    private BigDecimal FC;

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
