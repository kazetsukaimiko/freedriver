package io.freedriver.victron;

import io.freedriver.math.number.ScaledNumber;
import io.freedriver.victron.vedirect.OffReason;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static io.freedriver.math.UnitPrefix.*;

/**
 * Known fields coming from VE.Direct serial output and how to parse them.
 */
public enum VEDirectColumn {
    PRODUCT_ID("PID", Column.enumByCodeOptional(VictronProduct::byProductId, VEDirectMessage::setProductType, VEDirectMessage::getProductType)),
    FIRMWARE_VERSION_16_BIT("FW", Column.of(FirmwareVersion::new, VEDirectMessage::setFirmwareVersion, VEDirectMessage::getFirmwareVersion)),
    SERIAL_NUMBER("SER#", Column.string(VEDirectMessage::setSerialNumber, VEDirectMessage::getSerialNumber)),
    MAIN_VOLTAGE("V", Column.voltage(MILLI::volts, VEDirectMessage::setMainVoltage, VEDirectMessage::getMainVoltage)),
    MAIN_CURRENT("I", Column.amperage(MILLI::amps, VEDirectMessage::setMainCurrent, VEDirectMessage::getMainCurrent)),
    PANEL_VOLTAGE("VPV", Column.voltage(MILLI::volts, VEDirectMessage::setPanelVoltage, VEDirectMessage::getPanelVoltage)),
    PANEL_POWER("PPV", Column.wattage(ONE::watts, VEDirectMessage::setPanelPower, VEDirectMessage::getPanelPower)),
    STATE_OF_OPERATION("CS", Column.enumByCodeOptional(StateOfOperation::byCode, VEDirectMessage::setStateOfOperation, VEDirectMessage::getStateOfOperation)),
    TRACKER_OPERATION_MODE("MPPT", Column.enumByCodeOptional(TrackerOperation::byCode, VEDirectMessage::setTrackerOperation, VEDirectMessage::getTrackerOperation)),
    OFF_REASON("OR", Column.enumByCodeOptional(OffReason::byReasonCode, VEDirectMessage::setOffReason, VEDirectMessage::getOffReason)),
    ERROR_CODE("ERR", Column.enumByCodeOptional(ErrorCode::byCode, VEDirectMessage::setErrorCode, VEDirectMessage::getErrorCode)),
    LOAD_OUTPUT_STATE("LOAD", Column.enumOptional(LoadOutputState::byName, VEDirectMessage::setLoadOutputState, VEDirectMessage::getLoadOutputState)),
    RELAY_STATE("Relay", Column.enumOptional(RelayState::byName, VEDirectMessage::setRelayState, VEDirectMessage::getRelayState)),
    RESETTABLE_YIELD_TOTAL("H19", Column.energy(d -> KILO.wattHours(d.multiply(ScaledNumber.of(0.01))), VEDirectMessage::setResettableYield, VEDirectMessage::getResettableYield)),
    YIELD_TODAY("H20", Column.energy(d -> KILO.wattHours(d.multiply(ScaledNumber.of(0.01))), VEDirectMessage::setYieldToday, VEDirectMessage::getYieldToday)),
    MAX_POWER_TODAY("H21", Column.power(ONE::watts, VEDirectMessage::setMaxPowerToday, VEDirectMessage::getMaxPowerToday)),
    YIELD_YESTERDAY("H22", Column.energy(d -> KILO.wattHours(d.multiply(ScaledNumber.of(0.01))), VEDirectMessage::setYieldYesterday, VEDirectMessage::getYieldYesterday)),
    MAX_POWER_YESTERDAY("H23", Column.power(ONE::watts, VEDirectMessage::setMaxPowerYesterday, VEDirectMessage::getMaxPowerYesterday)),
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
