package io.freedriver.daly.bms;

import io.freedriver.serial.stream.api.Byteable;

import java.util.Objects;

public enum DalyCommand implements Byteable {
    READ(0xA5),
    WRITE(0x5A)
    ;

    private final byte value;

    DalyCommand(int value) {
        this((byte) (value & 0xFF));
    }

    DalyCommand(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public boolean matches(Byte aByte) {
        return Objects.equals(value, aByte);
    }

    @Override
    public byte[] asByteArray() {
        return new byte[] { getValue() };
    }
}
