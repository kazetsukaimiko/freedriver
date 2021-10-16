package io.freedriver.daly.bms;

import io.freedriver.serial.stream.api.Byteable;

import java.util.Objects;
import java.util.stream.Stream;

public enum Address implements Byteable {
    BMS_MASTER(0x01),
    BLUETOOTH_APP(0x20),
    GPRS(0x40),
    UPPER(0x80), // The Computer,
    UNKNOWN(0xFF)
    ;

    private final byte value;

    Address(int value) {
        this((byte)value);
    }

    Address(byte value) {
        this.value = value;
    }

    public static Address ofByte(byte b) {
        return Stream.of(values())
                .filter(address -> address.matches(b))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public boolean matches(byte b) {
        return Objects.equals(value, b);
    }

    public byte getValue() {
        return value;
    }

    @Override
    public byte[] asByteArray() {
        return new byte[] { getValue() };
    }
}
