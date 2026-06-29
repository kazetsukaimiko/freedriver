package io.freedriver.daly.bms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import io.freedriver.serial.stream.api.Streamable;

public enum Flag implements Streamable {
    START(0xDD),
    END(0x77)
    ;

    private final int value;

    Flag(int value) {
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

    }
}
