package io.freedriver.victron.hex;

public enum HexFlag {
    UNKNOWN_ID(0x01),
    NOT_SUPPORTED(0x02),
    PARAMETER_ERROR(0x04),
        ;


    private final byte value;

    HexFlag(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
