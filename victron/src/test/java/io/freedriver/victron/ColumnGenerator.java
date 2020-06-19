package io.freedriver.victron;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.Measurement;
import io.freedriver.victron.vedirect.OffReason;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum ColumnGenerator {
    PRODUCT_ID(VEDirectColumn.PRODUCT_ID, () -> randomMember(VictronProduct.values(), random()).getProductIdHex()),
    FIRMWARE_VERSION_16_BIT(VEDirectColumn.FIRMWARE_VERSION_16_BIT, () -> randomFirmwareVersion(random())),
    SERIAL_NUMBER(VEDirectColumn.SERIAL_NUMBER, () -> randomString(random())),
    MAIN_VOLTAGE(VEDirectColumn.MAIN_VOLTAGE, () -> randomMiliVoltage(random())),
    MAIN_CURRENT(VEDirectColumn.MAIN_CURRENT, () -> randomMiliAmperage(random())),
    PANEL_VOLTAGE(VEDirectColumn.PANEL_VOLTAGE, () -> randomMiliVoltage(random())),
    PANEL_POWER(VEDirectColumn.PANEL_POWER, () -> randomScaledValue(UnitPrefix.ONE, UnitPrefix::watts, random())),
    STATE_OF_OPERATION(VEDirectColumn.STATE_OF_OPERATION, () -> String.valueOf(randomMember(StateOfOperation.values(), random()).getCode())),
    TRACKER_OPERATION_MODE(VEDirectColumn.TRACKER_OPERATION_MODE, () -> String.valueOf(randomMember(TrackerOperation.values(), random()).getCode())),
    OFF_REASON(VEDirectColumn.OFF_REASON, () -> String.valueOf(randomMember(OffReason.values(), random()).getReasonCode())),
    ERROR_CODE(VEDirectColumn.ERROR_CODE, () -> String.valueOf(randomMember(ErrorCode.values(), random()).getCode())),
    LOAD_OUTPUT_STATE(VEDirectColumn.LOAD_OUTPUT_STATE, () -> randomMember(LoadOutputState.values(), random()).name()),
    RELAY_STATE(VEDirectColumn.RELAY_STATE, () -> randomMember(RelayState.values(), random()).name()),
    RESETTABLE_YIELD_TOTAL(VEDirectColumn.RESETTABLE_YIELD_TOTAL, () -> BigInteger.valueOf(random().nextInt(1000)).toString()),
    YIELD_TODAY(VEDirectColumn.YIELD_TODAY, () -> BigInteger.valueOf(random().nextInt(1000)).toString()),
    MAX_POWER_TODAY(VEDirectColumn.MAX_POWER_TODAY, () -> randomScaledValue(UnitPrefix.ONE, UnitPrefix::watts, random())),
    YIELD_YESTERDAY(VEDirectColumn.YIELD_YESTERDAY, () -> randomScaledValue(UnitPrefix.ONE, UnitPrefix::wattHours, random())),
    MAX_POWER_YESTERDAY(VEDirectColumn.MAX_POWER_YESTERDAY, () -> randomScaledValue(UnitPrefix.ONE, UnitPrefix::watts, random())),
    CHECKSUM(VEDirectColumn.CHECKSUM, () -> "x");

    private final VEDirectColumn column;
    private final Supplier<VEDirectColumnValue> generator;

    ColumnGenerator(VEDirectColumn column, Supplier<String> generator) {
        this.column = column;
        this.generator = () -> new VEDirectColumnValue(column, generator.get());
    }

    public VEDirectColumnValue create() {
        return generator.get();
    }

    public static Optional<ColumnGenerator> ofVEDirectColumn(VEDirectColumn column) {
        return Stream.of(values())
                .filter(columnGenerator -> columnGenerator.column == column)
                .findFirst();
    }



    /*
     * HELPERS
     */

    static Random random() {
        return new Random(System.currentTimeMillis());
    }

    static <T> T randomMember(T[] array, Random r) {
        return array[r.nextInt(array.length)];
    }

    static BigDecimal randomBigDecimal(Random r) {
        return new BigDecimal(r.nextInt(10000)).divide(new BigDecimal(1).pow(r.nextInt(3)));
    }

    static String randomMiliAmperage(Random r) {
        return randomScaledValue(UnitPrefix.MILLI, UnitPrefix::amps, r);
    }

    static String randomMiliVoltage(Random r) {
        return randomScaledValue(UnitPrefix.MILLI, UnitPrefix::volts, r);
    }

    static String randomScaledValue(UnitPrefix m, BiFunction<UnitPrefix, BigDecimal, Measurement<?>> method, Random r) {
        return method.apply(m, randomBigDecimal(r))
                .getValue()
                .getValue()
                .toBigInteger()
                .toString();
    }

    static String randomFirmwareVersion(Random r) {
        return randomMember(new String[]{
            "C208", "208", "0208FF", "020801"
        }, r);
    }


    private static String randomString(Random random) {
        return UUID.randomUUID().toString();
    }

}
