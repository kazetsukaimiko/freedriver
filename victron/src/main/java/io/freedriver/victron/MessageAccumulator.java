package io.freedriver.victron;

import io.freedriver.serial.stream.api.Accumulator;
import io.freedriver.serial.stream.api.SerialStream;

import java.io.IOException;

public class MessageAccumulator implements Accumulator<VEDirectMessage> {
    private static final ColumnValueAccumulator SOURCE = new ColumnValueAccumulator();
    @Override
    public VEDirectMessage apply(SerialStream stream) throws IOException {
        VEDirectMessage message = new VEDirectMessage();
        VEDirectColumnValue cV = SOURCE.apply(stream);
        while (cV.getColumn() != VEDirectColumn.CHECKSUM) {
            cV.apply(message);
            cV = SOURCE.apply(stream);
        }
        return message;
    }
}
