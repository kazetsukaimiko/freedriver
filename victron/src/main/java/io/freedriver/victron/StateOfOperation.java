package io.freedriver.victron;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public enum StateOfOperation {
    OFF(0, "Off"),
    LOW_POWER(1, "Low power"),
    FAULT(2, "Fault"),
    BULK(3, "Bulk"),
    ABSORPTION(4, "Absorption"),
    FLOAT(5, "Float"),
    STORAGE(6, "Storage"),
    EQUALIZE_MANUAL(7, "Equalize (manual)"),
    INVERTING(9, "Inverting"),
    POWER_SUPPLY(11, "Power supply"),
    STARTING_UP(245, "Starting-up"),
    REPEATED_ABSORPTION(246, "Repeated Absorption"),
    AUTO_EQUALIZE_RECONDITION(247, "Auto equalize / Recondition"),
    BATTERY_SAFE(248, "BatterySafe"),
    EXTERNAL_CONTROL(252, "External Control")
    ;

    private final int code;
    private final String description;

    StateOfOperation(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    static Optional<StateOfOperation> byCode(int code) {
        return Stream.of(StateOfOperation.values())
                .filter(stateOfOperation -> Objects.equals(stateOfOperation.getCode(), code))
                .findFirst();
    }

    static Optional<StateOfOperation> byName(String name) {
        return Stream.of(StateOfOperation.values())
                .filter(stateOfOperation -> Objects.equals(stateOfOperation.name(), name.toUpperCase()))
                .findFirst();
    }
}
