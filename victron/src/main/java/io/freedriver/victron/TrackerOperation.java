package io.freedriver.victron;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public enum TrackerOperation {
    OFF(0, "Off"),
    VOLTAGE_OR_CURRENT_LIMITED(1, "Voltage or current limited"),
    MPP_TRACKER_ACTIVE(2, "MPP Tracker active")
    ;

    private final int code;
    private final String description;

    TrackerOperation(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    static Optional<TrackerOperation> byCode(int code) {
        return Stream.of(TrackerOperation.values())
                .filter(trackerOperation -> Objects.equals(trackerOperation.getCode(), code))
                .findFirst();
    }

    static Optional<TrackerOperation> byName(String name) {
        return Stream.of(TrackerOperation.values())
                .filter(trackerOperation -> Objects.equals(trackerOperation.name(), name.toUpperCase()))
                .findFirst();
    }
}
