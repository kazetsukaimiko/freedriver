package io.freedriver.daly.bms;

import io.freedriver.daly.bms.exception.UnknownCommandException;
import io.freedriver.serial.stream.api.Streamable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

public enum QueryId implements Streamable {
    // TODO : Proper command lengths
    SOC(0x90, 8),
    MIN_MAX_VOLTAGES(0x91, 6),
    MIN_MAX_TEMPS(0x92, 4),
    MOS_STATUS_INFO(0x93, 8),
    STATUS_INFO(0x94, 8),
    CELL_VOLTAGE(0x95, 8),
    CELL_TEMP(0x96, 8),
    EQUILIBRIUM_STATUS(0x97, 8),
    FAILURE_STATUS(0x98, 8)
;


    private final int value;
    private final int responseLength;

    QueryId(int value, int responseLength) {
        this.value = value;
        this.responseLength = responseLength;
    }

    public int getValue() {
        return value;
    }

    public int getResponseLength() {
        return responseLength;
    }

    public static QueryId ofByte(byte b) {
        return Stream.of(values())
                .filter(commandId -> commandId.value == b)
                .findFirst()
                .orElseThrow(() -> new UnknownCommandException(b));
    }


    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(getValue());
    }
}
