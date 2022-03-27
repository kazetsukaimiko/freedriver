package io.freedriver.daly.bms;

import io.freedriver.serial.stream.api.Streamable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.stream.Stream;

public enum Address implements Streamable {
    BMS_MASTER(0x01),
    BLUETOOTH_APP(0x20),
    GPRS(0x40),
    UPPER(0x80), // The Computer,
    UNKNOWN(0xFF)
    ;

    private final int value;

    Address(int value) {
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

    public int getValue() {
        return value;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(getValue());
    }
}
