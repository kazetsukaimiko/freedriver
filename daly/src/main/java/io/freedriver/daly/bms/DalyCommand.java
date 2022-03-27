package io.freedriver.daly.bms;

import io.freedriver.serial.stream.api.Streamable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public enum DalyCommand implements Streamable {
    READ(0xA5),
    WRITE(0x5A)
    ;

    private final int value;

    DalyCommand(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean matches(Integer aByte) {
        return Objects.equals(value, aByte);
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(getValue());
    }
}
