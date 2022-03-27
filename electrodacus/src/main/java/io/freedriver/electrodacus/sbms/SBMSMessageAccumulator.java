package io.freedriver.electrodacus.sbms;

import io.freedriver.base.util.accumulator.ByteArrayAccumulator;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

public class SBMSMessageAccumulator extends ByteArrayAccumulator<SBMSMessage> {
    private final Path path;

    public SBMSMessageAccumulator(Path path) {
        this.path = path;
    }

    @Override
    public boolean isComplete(ByteArrayOutputStream input) {
        byte[] buffer = input.toByteArray();
        return buffer.length > 0 && buffer[buffer.length-1] == '\n';
    }

    @Override
    public SBMSMessage convert(ByteArrayOutputStream input) {
        return SBMSMessage.of(path, input.toByteArray()).orElse(null);
    }
}
