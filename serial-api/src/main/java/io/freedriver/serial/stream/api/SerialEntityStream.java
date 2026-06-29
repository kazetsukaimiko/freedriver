package io.freedriver.serial.stream.api;

import java.io.ByteArrayOutputStream;

import io.freedriver.base.util.EntityStreamWithOutput;
import io.freedriver.base.util.accumulator.Accumulator;
import io.freedriver.serial.api.SerialResource;

public class SerialEntityStream<R> extends EntityStreamWithOutput<R> {

    public SerialEntityStream(SerialResource resource, Accumulator<ByteArrayOutputStream, R> accumulator) {
        super(new SerialInputStream(resource), new SerialOutputStream(resource), accumulator);
    }
}
