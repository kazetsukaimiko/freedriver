package io.freedriver.electrodacus.sbms;

import io.freedriver.serial.stream.api.Accumulator;
import io.freedriver.serial.stream.api.SerialStream;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public class SBMSAccumulator implements Accumulator<SBMSMessage> {
    private final Path path;

    public SBMSAccumulator(Path path) {
        this.path = path;
    }

    @Override
    public SBMSMessage apply(SerialStream stream) throws IOException {
        return SBMSMessage.of(path, stream.nextLine(Duration.ofSeconds(1)).getBytes())
                .orElse(null);
    }
}
