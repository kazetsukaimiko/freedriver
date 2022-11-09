package io.freedriver.generty.kibana;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.freedriver.generty.model.FansSection;
import io.freedriver.generty.model.InputsSection;
import io.freedriver.generty.model.OutputsSection;
import io.freedriver.generty.model.SetupSection;
import io.freedriver.generty.model.StatsSection;
import io.freedriver.generty.model.Statsjson;
import io.freedriver.generty.model.TempsSection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Flattened version of stats.json to make it easier for kibana to visualize.
 */
public class KibanaStats implements Comparable<KibanaStats>{
    private static final Logger LOGGER = Logger.getLogger(KibanaStats.class.getName());
    private String inverterId;
    private String inverterModel;

    private BigDecimal inV;
    private BigDecimal inA;
    private BigDecimal xfA;
    private BigDecimal battV;

    private BigDecimal outV;
    private BigDecimal outA;
    private BigDecimal outW;
    private BigDecimal outPF;
    private BigDecimal outHZ;
    private BigDecimal xfEFF;

    private BigDecimal inverterWatts;
    private BigDecimal inverterLoad;
    private BigDecimal chargeWatts;

    private BigDecimal lifetimeWh;

    private String rdx;
    private BigDecimal TTA;
    private BigDecimal TMA;
    private BigDecimal TTB;
    private BigDecimal TMB;

    private BigDecimal FA;
    private BigDecimal FB;
    private BigDecimal FC;

    private BigDecimal fanAverage;



    @JsonProperty("@timestamp")
    private Instant timestamp = Instant.now();


    public KibanaStats(String inverterId, String inverterModel, BigDecimal inV, BigDecimal inA, BigDecimal xfA, BigDecimal battV, BigDecimal outV, BigDecimal outA, BigDecimal outW, BigDecimal outPF, BigDecimal outHZ, BigDecimal xfEFF, BigDecimal inverterWatts, BigDecimal inverterLoad, BigDecimal chargeWatts, BigDecimal lifetimekWh, String rdx, BigDecimal TTA, BigDecimal TMA, BigDecimal TTB, BigDecimal TMB, BigDecimal FA, BigDecimal FB, BigDecimal FC, BigDecimal fanAverage, Instant timestamp) {
        this.inverterId = inverterId;
        this.inverterModel = inverterModel;
        this.inV = inV;
        this.inA = inA;
        this.xfA = xfA;
        this.battV = battV;
        this.outV = outV;
        this.outA = outA;
        this.outW = outW;
        this.outPF = outPF;
        this.outHZ = outHZ;
        this.xfEFF = xfEFF;
        this.inverterWatts = inverterWatts;
        this.inverterLoad = inverterLoad;
        this.chargeWatts = chargeWatts;
        this.lifetimeWh = lifetimekWh;
        this.rdx = rdx;
        this.TTA = TTA;
        this.TMA = TMA;
        this.TTB = TTB;
        this.TMB = TMB;
        this.FA = FA;
        this.FB = FB;
        this.FC = FC;
        this.fanAverage = fanAverage;
        this.timestamp = timestamp;
    }

    public KibanaStats() {
    }

    public KibanaStats(String inverterId, SetupSection setup, InputsSection inputs, OutputsSection outputs, StatsSection stats, TempsSection temps, FansSection fans) {
        this(
                inverterId,
                setup.getModel(),

                inputs.getInV(),
                inputs.getInA(),
                inputs.getXfA(),
                inputs.getBattV(),

                outputs.getOutV(),
                outputs.getOutA(),
                outputs.getOutW(),
                outputs.getOutPF(),
                outputs.getOutHZ(),
                outputs.getXfEFF().movePointLeft(2),

                calculateInverterWatts(inputs, outputs),
                calculateInverterLoad(setup, inputs, outputs),
                calculateChargeWatts(inputs),

                stats.getKWh().movePointRight(3),

                temps.getRdx(),
                temps.getTTA(),
                temps.getTMA(),
                temps.getTTB(),
                temps.getTMB(),

                fans.getFA().movePointLeft(2),
                fans.getFB().movePointLeft(2),
                fans.getFC().movePointLeft(2),
                calculateFanAverage(fans).movePointLeft(2),
                Instant.now()
        );
    }

    public KibanaStats(String inverterId, Statsjson statsjson) {
        this(inverterId, statsjson.getSetup(), statsjson.getInputs(), statsjson.getOutputs(), statsjson.getStats(), statsjson.getTemps(), statsjson.getFans());
    }

    public String getInverterId() {
        return inverterId;
    }

    public void setInverterId(String inverterId) {
        this.inverterId = inverterId;
    }

    public String getInverterModel() {
        return inverterModel;
    }

    public void setInverterModel(String inverterModel) {
        this.inverterModel = inverterModel;
    }

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

    public void setBattV(BigDecimal battV) {
        this.battV = battV;
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

    public BigDecimal getInverterWatts() {
        return inverterWatts;
    }

    public void setInverterWatts(BigDecimal inverterWatts) {
        this.inverterWatts = inverterWatts;
    }

    public BigDecimal getInverterLoad() {
        return inverterLoad;
    }

    public void setInverterLoad(BigDecimal inverterLoad) {
        this.inverterLoad = inverterLoad;
    }

    public BigDecimal getChargeWatts() {
        return chargeWatts;
    }

    public void setChargeWatts(BigDecimal chargeWatts) {
        this.chargeWatts = chargeWatts;
    }

    public BigDecimal getLifetimeWh() {
        return lifetimeWh;
    }

    public void setLifetimeWh(BigDecimal lifetimeWh) {
        this.lifetimeWh = lifetimeWh;
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

    public BigDecimal getFanAverage() {
        return fanAverage;
    }

    public void setFanAverage(BigDecimal fanAverage) {
        this.fanAverage = fanAverage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }


    public String getMenuText() {
        return inverterId + "\n / Load: " + getInverterLoad().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) +"% / " + getInverterWatts() + "W "   +")";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KibanaStats that = (KibanaStats) o;
        return Objects.equals(inverterId, that.inverterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inverterId);
    }

    @Override
    public int compareTo(KibanaStats kibanaStats) {
        return timestamp.compareTo(kibanaStats.timestamp);
    }

    private static BigDecimal calculateInverterLoad(SetupSection setupSection, InputsSection inputs, OutputsSection outputs) {
        return calculateInverterWatts(inputs, outputs)
                .setScale(4, RoundingMode.HALF_UP)
                .divide(new BigDecimal(getInverterRating(setupSection)), RoundingMode.HALF_UP);
    }

    private static int getInverterRating(SetupSection setupSection) {
        return Optional.ofNullable(setupSection)
                .map(SetupSection::getModel)
                .flatMap(KibanaStats::extractRating)
                .orElse(6000);
    }

    private static Optional<Integer> extractRating(String modelString) {
        String[] parts = modelString.split("-");
        if (parts.length >= 2) {
            try {
                return Optional.of(Integer.parseInt(parts[1]));
            } catch (NumberFormatException numberFormatException) {
                LOGGER.log(Level.WARNING, "Cannot ascertain inverter rating as model string is malformed: " + parts[1], numberFormatException);
            }
        }
        return Optional.empty();
    }

    private static BigDecimal calculateChargeWatts(InputsSection inputs) {
        return !isPositive(inputs.getXfA())
                ? inputs.getXfA().abs().multiply(inputs.getInV())
                : BigDecimal.ZERO;
    }

    private static BigDecimal calculateFanAverage(FansSection fans) {
        return fans.getFA().add(fans.getFB()).add(fans.getFC()).divide(new BigDecimal(3), RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateInverterWatts(InputsSection inputs, OutputsSection outputsSection) {
        return isPositive(inputs.getXfA())
                ? outputsSection.getOutW()
                : BigDecimal.ZERO;
    }

    private static boolean isPositive(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }

}
