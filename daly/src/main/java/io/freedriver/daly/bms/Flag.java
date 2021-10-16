package io.freedriver.daly.bms;

import io.freedriver.serial.stream.api.Byteable;

import java.util.Objects;

public enum Flag implements Byteable {
    START(0xDD),
    END(0x77)
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

    @Override
    public byte[] asByteArray() {
        return new byte[] { getValue() };
    }
}
