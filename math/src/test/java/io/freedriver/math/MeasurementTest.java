package io.freedriver.math;

import io.freedriver.math.measurement.types.Measurement;
import io.freedriver.math.number.ScaledNumber;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

public abstract class MeasurementTest<M extends Measurement<M>> extends RandomizedTest {
    public static final BigDecimal GO_ONE_LOWER = new BigDecimal("0.1");
    public static final BigDecimal GO_ONE_LOWER_2 = new BigDecimal("0.01");
    public static final BigDecimal GO_ONE_LOWER_3 = new BigDecimal("0.001");
    public static final BigDecimal GO_ONE_LOWER_4 = new BigDecimal("0.00123");

    public static final BigDecimal GO_ONE_HIGHER = new BigDecimal("1000");
    public static final BigDecimal GO_ONE_HIGHER_2 = new BigDecimal("100000");

    public static final BigDecimal GO_TWO_HIGHER = new BigDecimal("1000000");
    public static final BigDecimal GO_TWO_LOWER = new BigDecimal("0.000001");

    protected abstract M construct(ScaledNumber scaledNumber);

    @Test
    public void testPrintAllExamplesAndMultipliers() {
        allExamplesAllMultipliers()
                .map(m -> m.getFullUnit() + " vs " + m.normalize().getFullUnit())
                .forEach(System.out::println);
    }

    @Test
    public void testBigDecimalStats() {
        Stream.of(new BigDecimal("1234.12340000").setScale(20, RoundingMode.UNNECESSARY), new BigDecimal("1234"), GO_ONE_HIGHER, GO_ONE_LOWER, GO_ONE_LOWER_2, GO_ONE_LOWER_3, GO_ONE_LOWER_4)
                .forEach(this::printStats);
    }


    public void printStats(BigDecimal bd) {
        StringBuilder sb = new StringBuilder(bd.toPlainString());
        sb.append(" precision:");
        sb.append(bd.precision());
        sb.append(";scale:");
        sb.append(bd.scale());
        sb.append(";signum:");
        sb.append(bd.signum());
        sb.append(";zeroesAfter:");
        sb.append(bd.scale()-bd.precision());
        System.out.println(sb.toString());
    }







    /*
     * HELPERS
     */
    public Stream<UnitPrefix> allUnitPrefixes() {
        return Stream.of(UnitPrefix.values());
    }

    public Stream<BigDecimal> allExamples() {
        return Stream.of(GO_ONE_HIGHER);
                //Stream.of(BigDecimal.ZERO, GO_ONE_HIGHER_2, GO_ONE_HIGHER, GO_ONE_LOWER_2, GO_ONE_LOWER, GO_TWO_HIGHER, GO_TWO_LOWER);
    }

    public Stream<M> allExamplesAllMultipliers() {
        return allUnitPrefixes()
                .flatMap(multi -> allExamples().map(ex ->
                        construct(ScaledNumber.of(ex, multi))));
    }

}
