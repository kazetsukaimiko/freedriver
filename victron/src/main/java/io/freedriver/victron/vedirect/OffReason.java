package io.freedriver.victron.vedirect;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public enum OffReason {
    NO_INPUT_POWER(0x00000001),
    SWITCHED_OFF_BY_POWER_SWITCH(0x00000002),
    SWITCHED_OFF_BY_DEVICE_MODE_REGISTER(0x00000004),
    REMOTE_INPUT(0x00000008),
    PROTECTION_INACTIVE(0x00000010),
    PAYGO(0x00000020),
    BMS(0x00000040),
    ENGINE_SHUTDOWN_DETECTION(0x00000080),
    ANALYZING_INPUT_VOLTAGE(0x00000100),
    UNKNOWN(0xFFFFFFFF);

    private final long reasonCode;

    OffReason(long reasonCode) {
        this.reasonCode = reasonCode;
    }

    public long getReasonCode() {
        return reasonCode;
    }

    public static Optional<OffReason> byReasonCode(long reasonCode) {
        return Stream.of(OffReason.values())
                .filter(offReason -> Objects.equals(offReason.getReasonCode(), reasonCode))
                .findFirst();
    }
}
