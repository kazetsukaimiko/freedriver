package io.freedriver.daly.bms;

import java.util.Objects;

public enum Flag {
    START(0xA5),
    ;

    private final byte value;

    Flag(int value) {
        this((byte) (value & 0xFF));
    }

    Flag(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public boolean matches(Byte aByte) {
        return Objects.equals(value, aByte);
    }
}
