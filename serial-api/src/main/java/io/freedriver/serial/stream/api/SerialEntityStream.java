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
     * Closes both byte streams, each of which closes the shared port, then closes
     * the port again. {@link SerialResource#close()} is idempotent, so a failed
     * UUID handshake cannot leave the device open.
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
