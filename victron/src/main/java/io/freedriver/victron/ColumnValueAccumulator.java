package io.freedriver.victron;

import io.freedriver.serial.stream.api.Accumulator;
import io.freedriver.serial.stream.api.Accumulators;
import io.freedriver.serial.stream.api.SerialStream;

import java.io.IOException;
import java.util.Optional;

public class ColumnValueAccumulator implements Accumulator<VEDirectColumnValue> {
    private static final Accumulator<String> SOURCE = Accumulators.linesString();
    @Override
    public VEDirectColumnValue apply(SerialStream stream) throws IOException {
        Optional<VEDirectColumnValue> value = Optional.empty();
        while (!stream.isClosed()) {
            value = VEDirectColumnValue.fromSerial(SOURCE.apply(stream));
            if (value.isPresent()) {
                break;
            }
        }
        return value.orElse(null);
    }
}
