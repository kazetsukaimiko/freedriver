package io.freedriver.serial.stream.api;

import java.io.ByteArrayOutputStream;

import io.freedriver.base.util.EntityStreamWithOutput;
import io.freedriver.base.util.accumulator.Accumulator;
import io.freedriver.serial.api.SerialResource;

public class SerialEntityStream<R> extends EntityStreamWithOutput<R> {
    private final SerialResource resource;

    public SerialEntityStream(SerialResource resource, Accumulator<ByteArrayOutputStream, R> accumulator) {
        super(new SerialInputStream(resource), new SerialOutputStream(resource), accumulator);
        this.resource = resource;
    }

    /**
     * Releases the shared serial port. The byte streams do not close it, so a failed
     * UUID handshake would otherwise leave the port open and the next open reports
     * "Port busy".
     */
    @Override
    public void close() throws Exception {
        try {
            super.close();
        } finally {
            resource.close();
        }
    }
}
