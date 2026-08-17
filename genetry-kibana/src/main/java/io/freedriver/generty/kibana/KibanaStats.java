package io.freedriver.generty.kibana;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.freedriver.generty.model.FansSection;
import io.freedriver.generty.model.InputsSection;
import io.freedriver.generty.model.OutputsSection;
import io.freedriver.generty.model.SetupSection;
import io.freedriver.generty.model.StatsSection;
import io.freedriver.generty.model.Statsjson;
import io.freedriver.generty.model.TempsSection;
import lombok.Builder;

/**
 * A Flattened version of stats.json to make it easier for kibana to visualize.
 */
@Builder(toBuilder = true)
public record KibanaStats(
        String inverterId,
        String inverterModel,
        BigDecimal inV,
        BigDecimal inA,
        BigDecimal xfA,
        BigDecimal battV,
        BigDecimal outV,
        BigDecimal outA,
        BigDecimal outW,
        BigDecimal outPF,
        BigDecimal outHZ,
        BigDecimal xfEFF,
        BigDecimal inverterWatts,
        BigDecimal inverterLoad,
        BigDecimal chargeWatts,
        BigDecimal lifetimeWh,
        String rdx,
        BigDecimal TTA,
        BigDecimal TMA,
        BigDecimal TTB,
        BigDecimal TMB,
        BigDecimal FA,
        BigDecimal FB,
        BigDecimal FC,
        BigDecimal fanAverage,
        @JsonProperty("@timestamp") Instant timestamp) implements Comparable<KibanaStats> {
    private static final Logger LOGGER = Logger.getLogger(KibanaStats.class.getName());

    public KibanaStats {
        timestamp = timestamp == null ? Instant.now() : timestamp;
    }

    public KibanaStats(String inverterId, SetupSection setup, InputsSection inputs, OutputsSection outputs, StatsSection stats, TempsSection temps, FansSection fans) {
        this(
                inverterId,
                setup.model(),

                inputs.inV(),
                inputs.inA(),
                inputs.xfA(),
                inputs.battV(),

                outputs.outV(),
                outputs.outA(),
                outputs.outW(),
                outputs.outPF(),
                outputs.outHZ(),
                outputs.xfEFF().movePointLeft(2),

                calculateInverterWatts(inputs, outputs),
                calculateInverterLoad(setup, inputs, outputs),
                calculateChargeWatts(inputs),

                stats.KWh().movePointRight(3),

                temps.rdx(),
                temps.TTA(),
                temps.TMA(),
                temps.TTB(),
                temps.TMB(),

                fans.FA().movePointLeft(2),
                fans.FB().movePointLeft(2),
                fans.FC().movePointLeft(2),
                calculateFanAverage(fans).movePointLeft(2),
                Instant.now()
        );
    }

    public KibanaStats(String inverterId, Statsjson statsjson) {
        this(inverterId, statsjson.setup(), statsjson.inputs(), statsjson.outputs(), statsjson.stats(), statsjson.temps(), statsjson.fans());
    }

    public String getMenuText() {
        return inverterId + "\n / Load: " + inverterLoad.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) +"% / " + inverterWatts + "W "   +")";
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
                .map(SetupSection::model)
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
        return !isPositive(inputs.xfA())
                ? inputs.xfA().abs().multiply(inputs.inV())
                : BigDecimal.ZERO;
    }

    private static BigDecimal calculateFanAverage(FansSection fans) {
        return fans.FA().add(fans.FB()).add(fans.FC()).divide(new BigDecimal(3), RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateInverterWatts(InputsSection inputs, OutputsSection outputsSection) {
        return isPositive(inputs.xfA())
                ? outputsSection.outW()
                : BigDecimal.ZERO;
    }

    private static boolean isPositive(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }

}
