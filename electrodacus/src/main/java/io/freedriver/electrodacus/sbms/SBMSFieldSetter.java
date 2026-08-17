package io.freedriver.electrodacus.sbms;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.thermo.Temperature;
import io.freedriver.math.measurement.units.TemperatureScale;
import io.freedriver.math.number.ScaledNumber;

/**
 * The job of this class is to hydrate SBMSMessage objects with the data from the SBMS0.
 */
public enum SBMSFieldSetter {
    TIMESTAMP(SBMSFieldSetter::setTemporals),
    SOC(Double::parseDouble, (message, value) -> message.toBuilder().soc(value).build()),
    C1(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellOne(value).build()),
    C2(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellTwo(value).build()),
    C3(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellThree(value).build()),
    C4(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellFour(value).build()),
    C5(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellFive(value).build()),
    C6(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellSix(value).build()),
    C7(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellSeven(value).build()),
    C8(SBMSFieldSetter::milliVolts, (message, value) -> message.toBuilder().cellEight(value).build()),
    IT(SBMSFieldSetter::tempCelsuis, (message, value) -> message.toBuilder().internalTemperature(value).build()),
    ET(SBMSFieldSetter::tempCelsuis, (message, value) -> message.toBuilder().externalTemperature(value).build()),
    CHARGING(SBMSFieldSetter::plusMinusAsBoolean, (message, value) -> message.toBuilder().charging(value).build()),
    DISCHARGING(SBMSFieldSetter::plusMinusAsBooleanInv),
    CURRENT_mA(SBMSFieldSetter::milliAmps, (message, value) -> message.toBuilder().batteryCurrent(value).build()),
    PV1(SBMSFieldSetter::milliAmps, (message, value) -> message.toBuilder().pvCurrent1(value).build()),
    PV2(SBMSFieldSetter::milliAmps, (message, value) -> message.toBuilder().pvCurrent1(value).build()),
    EXT_LOAD_CURRENT(SBMSFieldSetter::milliAmps, (message, value) -> message.toBuilder().extCurrent(value).build()),

    /* Not yet implemented as I have no way of testing these!
    AD2,
    AD3,
     HT1,
    HT2,
     */

    ERR(SBMSFieldSetter::errorCodes, (message, value) -> message.toBuilder().errorCodes(value).build()),
    ;

    // Takes the full data, and populates field(s) on the SBMSMessage POJO with that data.
    private final BiFunction<SBMSMessage, byte[], SBMSMessage> valueSetter;

    // Convenience constructor to build a valueSetter based off transformer/setter method references.
    // Finds an SBMSField of the same name as our SBMSFieldSetter, using that to decode data[], transformer
    // to convert the string representation to the type on the POJO, and then setter to hydrate the POJO with the field
    // data.
    <T> SBMSFieldSetter(Function<String, T> transformer, BiFunction<SBMSMessage, T, SBMSMessage> setter) {
        SBMSField field = SBMSField.valueOf(name());
        this.valueSetter = (message, data) -> field.decodeToString(data)
                .map(value -> setter.apply(message, transformer.apply(value.value())))
                .orElse(message);
    }

    // Main constructor
    SBMSFieldSetter(BiFunction<SBMSMessage, byte[], SBMSMessage> setter) {
        this.valueSetter = setter;
    }

    // TODO: Parse data?
    private static SBMSMessage setTemporals(SBMSMessage message, byte[] data) {
        return message.toBuilder().timestamp(Instant.now()).build();
    }

    private static Potential milliVolts(String s) {
        return new Potential(new ScaledNumber(Double.parseDouble(s), UnitPrefix.MILLI).scaleTo(UnitPrefix.ONE));
    }

    private static Current milliAmps(String s) {
        return new Current(new ScaledNumber(Double.parseDouble(s), UnitPrefix.MILLI).scaleTo(UnitPrefix.ONE));
    }

    public static Temperature tempCelsuis(String s) {
        return new Temperature(new ScaledNumber(Double.parseDouble(s), UnitPrefix.ONE), TemperatureScale.CELSUIS);
    }

    private static boolean plusMinusAsBoolean(String s) {
        return Objects.equals(s, "+");
    }

    private static SBMSMessage plusMinusAsBooleanInv(SBMSMessage message, byte[] data) {
        return SBMSField.CHARGING.decodeToString(data)
                .map(charging -> message.toBuilder().discharging(!plusMinusAsBoolean(charging.value())).build())
                .orElse(message);
    }

    private static Set<ErrorCode> errorCodes(String s) {
        return ErrorCode.match(Double.parseDouble(s));
    }

    public static Stream<SBMSFieldSetter> stream() {
        return Stream.of(values());
    }

    public SBMSMessage apply(SBMSMessage sbmsMessage, byte[] data) {
        return this.valueSetter.apply(sbmsMessage, data);
    }
}
