package io.freedriver.electrodacus.sbms;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.thermo.Temperature;
import io.freedriver.math.measurement.units.TemperatureScale;
import io.freedriver.math.number.ScaledNumber;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The job of this class is to hydrate SBMSMessage objects with the data from the SBMS0.
 */
public enum SBMSFieldSetter {
    TIMESTAMP(SBMSFieldSetter::setTemporals),
    SOC(Double::parseDouble, SBMSMessage::setSoc),
    C1(SBMSFieldSetter::milliVolts, SBMSMessage::setCellOne),
    C2(SBMSFieldSetter::milliVolts, SBMSMessage::setCellTwo),
    C3(SBMSFieldSetter::milliVolts, SBMSMessage::setCellThree),
    C4(SBMSFieldSetter::milliVolts, SBMSMessage::setCellFour),
    C5(SBMSFieldSetter::milliVolts, SBMSMessage::setCellFive),
    C6(SBMSFieldSetter::milliVolts, SBMSMessage::setCellSix),
    C7(SBMSFieldSetter::milliVolts, SBMSMessage::setCellSeven),
    C8(SBMSFieldSetter::milliVolts, SBMSMessage::setCellEight),
    IT(SBMSFieldSetter::tempCelsuis, SBMSMessage::setInternalTemperature),
    ET(SBMSFieldSetter::tempCelsuis, SBMSMessage::setExternalTemperature),
    CHARGING(SBMSFieldSetter::plusMinusAsBoolean, SBMSMessage::setCharging),
    DISCHARGING(SBMSFieldSetter::plusMinusAsBooleanInv),
    CURRENT_mA(SBMSFieldSetter::milliAmps, SBMSMessage::setBatteryCurrent),
    PV1(SBMSFieldSetter::milliAmps, SBMSMessage::setPvCurrent1),
    PV2(SBMSFieldSetter::milliAmps, SBMSMessage::setPvCurrent1),
    EXT_LOAD_CURRENT(SBMSFieldSetter::milliAmps, SBMSMessage::setExtCurrent),

    /* Not yet implemented as I have no way of testing these!
    AD2,
    AD3,
     HT1,
    HT2,
     */

    ERR(SBMSFieldSetter::errorCodes, SBMSMessage::setErrorCodes),
    ;

    // Takes the full data, and populates field(s) on the SBMSMessage POJO with that data.
    private final BiConsumer<SBMSMessage, byte[]> valueSetter;

    // Convenience constructor to build a valueSetter based off transformer/setter method references.
    // Finds an SBMSField of the same name as our SBMSFieldSetter, using that to decode data[], transformer
    // to convert the string representation to the type on the POJO, and then setter to hydrate the POJO with the field
    // data.
    <T> SBMSFieldSetter(Function<String, T> transformer, BiConsumer<SBMSMessage, T> setter) {
        SBMSField field = SBMSField.valueOf(name());
        this.valueSetter = (message, data) -> field.decodeToString(data)
                .ifPresent(value -> setter.accept(message, transformer.apply(value.getValue())));
    }

    // Main constructor
    SBMSFieldSetter(BiConsumer<SBMSMessage, byte[]> setter) {
        this.valueSetter = setter;
    }

    // TODO: Parse data?
    private static void setTemporals(SBMSMessage message, byte[] data) {
        message.setTimestamp(Instant.now());
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

    private static void plusMinusAsBooleanInv(SBMSMessage message, byte[] data) {
        SBMSField.CHARGING.decodeToString(data)
                .ifPresent(charging -> message.setDischarging(!plusMinusAsBoolean(charging.getValue())));
    }

    private static Set<ErrorCode> errorCodes(String s) {
        return ErrorCode.match(Double.parseDouble(s));
    }

    public static Stream<SBMSFieldSetter> stream() {
        return Stream.of(values());
    }

    public void apply(SBMSMessage sbmsMessage, byte[] data) {
        this.valueSetter.accept(sbmsMessage, data);
    }
}
