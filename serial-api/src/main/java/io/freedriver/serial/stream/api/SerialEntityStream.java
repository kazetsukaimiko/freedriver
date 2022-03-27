package io.freedriver.serial.stream.api;

import io.freedriver.base.util.EntityStreamWithOutput;
import io.freedriver.base.util.accumulator.Accumulator;
import io.freedriver.base.util.EntityStream;
import io.freedriver.serial.api.SerialResource;

import java.io.ByteArrayOutputStream;

public class SerialEntityStream<R> extends EntityStreamWithOutput<R> {

    public SerialEntityStream(SerialResource resource, Accumulator<ByteArrayOutputStream, R> accumulator) {
        super(new SerialInputStream(resource), new SerialOutputStream(resource), accumulator);
    }
}
