package io.freedriver.victron;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public enum ErrorCode {
    NO_ERROR(0, "No error"),
    HIGH_BATTERY_VOLTAGE(2, "Battery voltage "),
    HIGH_CHARGER_TEMP(17, "Charger temperature too high"),
    HIGH_CHARGER_CURRENT(18, "Charger over current"),
    BULK_LIMIT_EXCEEDED(20, "Bulk time limit exceeded"),
    CURRENT_SENSOR_ISSUE(21, "Current sensor issue (sensor bias/sensor broken"),
    TERMINALS_OVERHEATED(26, "Terminals overheaded"),
    CONVERTER_ISSUE(28, "Converter issue (dual converter models only)"),
    HIGH_PANEL_VOLTAGE(33, "Input voltage too high (solar panel)"),
    HIGH_PANEL_CURRENT(34, "Input current too high (solar panel)"),
    HIGH_BATTERY_VOLTAGE_INPUT_SHUTDOWN(38, "Input shutdown (due to excessive battery voltage)"),
    OFF_CURRENT_FLOW_INPUT_SHUTDOWN(39, "Input shutdown (due to current flow during off mode)"),
    DEVICE_COMMUNICATION_LOST(65, "Lost communication with one of devices"),
    SYNC_CHARGING_DEVICE_ISSUE(66, "Synchronised charging device configuration issue"),
    BMS_CONNECTION_LOST(67, "BMS connection lost"),
    NETWORK_MISCONFIGURED(68, "Network misconfigured"),
    FACTORY_CALIBRATION_DATA_LOST(116, "Factory calibration data lost"),
    BAD_FIRMWARE(117, "Invalid/incompatible firmware"),
    USER_SETTINGS_INVALID(119, "User settings invalid")
    ;

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    static Optional<ErrorCode> byCode(int code) {
        return Stream.of(ErrorCode.values())
                .filter(errorCode -> Objects.equals(errorCode.getCode(), code))
                .findFirst();
    }

    static Optional<ErrorCode> byName(String name) {
        return Stream.of(ErrorCode.values())
                .filter(errorCode -> Objects.equals(errorCode.name(), name.toUpperCase()))
                .findFirst();
    }
}
