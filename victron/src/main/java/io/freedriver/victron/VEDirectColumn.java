package io.freedriver.victron;

import static io.freedriver.math.UnitPrefix.*;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import io.freedriver.math.number.ScaledNumber;
import io.freedriver.victron.vedirect.OffReason;

/**
 * Known fields coming from VE.Direct serial output and how to parse them.
 */
public enum VEDirectColumn {
    PRODUCT_ID("PID", Column.enumByCodeOptional(VictronProduct::byProductId, (msg, v) -> msg.toBuilder().productType(v).build(), VEDirectMessage::productType)),
    FIRMWARE_VERSION_16_BIT("FW", Column.of(FirmwareVersion::new, (msg, v) -> msg.toBuilder().firmwareVersion(v).build(), VEDirectMessage::firmwareVersion)),
    SERIAL_NUMBER("SER#", Column.string((msg, v) -> msg.toBuilder().serialNumber(v).build(), VEDirectMessage::serialNumber)),
    MAIN_VOLTAGE("V", Column.voltage(MILLI::volts, (msg, v) -> msg.toBuilder().mainVoltage(v).build(), VEDirectMessage::mainVoltage)),
    MAIN_CURRENT("I", Column.amperage(MILLI::amps, (msg, v) -> msg.toBuilder().mainCurrent(v).build(), VEDirectMessage::mainCurrent)),
    PANEL_VOLTAGE("VPV", Column.voltage(MILLI::volts, (msg, v) -> msg.toBuilder().panelVoltage(v).build(), VEDirectMessage::panelVoltage)),
    PANEL_POWER("PPV", Column.wattage(ONE::watts, (msg, v) -> msg.toBuilder().panelPower(v).build(), VEDirectMessage::panelPower)),
    STATE_OF_OPERATION("CS", Column.enumByCodeOptional(StateOfOperation::byCode, (msg, v) -> msg.toBuilder().stateOfOperation(v).build(), VEDirectMessage::stateOfOperation)),
    TRACKER_OPERATION_MODE("MPPT", Column.enumByCodeOptional(TrackerOperation::byCode, (msg, v) -> msg.toBuilder().trackerOperation(v).build(), VEDirectMessage::trackerOperation)),
    OFF_REASON("OR", Column.enumByCodeOptional(OffReason::byReasonCode, (msg, v) -> msg.toBuilder().offReason(v).build(), VEDirectMessage::offReason)),
    ERROR_CODE("ERR", Column.enumByCodeOptional(ErrorCode::byCode, (msg, v) -> msg.toBuilder().errorCode(v).build(), VEDirectMessage::errorCode)),
    LOAD_OUTPUT_STATE("LOAD", Column.enumOptional(LoadOutputState::byName, (msg, v) -> msg.toBuilder().loadOutputState(v).build(), VEDirectMessage::loadOutputState)),
    RELAY_STATE("Relay", Column.enumOptional(RelayState::byName, (msg, v) -> msg.toBuilder().relayState(v).build(), VEDirectMessage::relayState)),
    RESETTABLE_YIELD_TOTAL("H19", Column.energy(d -> KILO.wattHours(d.multiply(ScaledNumber.of(0.01))), (msg, v) -> msg.toBuilder().resettableYield(v).build(), VEDirectMessage::resettableYield)),
    YIELD_TODAY("H20", Column.energy(d -> KILO.wattHours(d.multiply(ScaledNumber.of(0.01))), (msg, v) -> msg.toBuilder().yieldToday(v).build(), VEDirectMessage::yieldToday)),
    MAX_POWER_TODAY("H21", Column.power(ONE::watts, (msg, v) -> msg.toBuilder().maxPowerToday(v).build(), VEDirectMessage::maxPowerToday)),
    YIELD_YESTERDAY("H22", Column.energy(d -> KILO.wattHours(d.multiply(ScaledNumber.of(0.01))), (msg, v) -> msg.toBuilder().yieldYesterday(v).build(), VEDirectMessage::yieldYesterday)),
    MAX_POWER_YESTERDAY("H23", Column.power(ONE::watts, (msg, v) -> msg.toBuilder().maxPowerYesterday(v).build(), VEDirectMessage::maxPowerYesterday)),
    CHECKSUM("Checksum", Column.doNothing())
    ;
    private final String columnName;
    private final Column<?> definition;

    VEDirectColumn(String columnName, Column<?> definition) {
        this.columnName = columnName;
        this.definition = definition;
    }

    public String getColumnName() {
        return columnName;
    }

    public Column<?> getDefinition() {
        return definition;
    }

    public static Optional<VEDirectColumn> byColumnName(String name) {
        return Stream.of(VEDirectColumn.values())
                .filter(veDirectColumn -> Objects.equals(veDirectColumn.getColumnName(), name))
                .findFirst();
    }

    public static Optional<VEDirectColumn> byName(String name) {
        return Stream.of(VEDirectColumn.values())
                .filter(veDirectColumn -> Objects.equals(veDirectColumn.name(), name))
                .findFirst();
    }

    @Override
    public String toString() {
        return "VEDirectColumn{" +
                "columnName='" + columnName + '\'' +
                ", populator=" + definition +
                '}';
    }
}
