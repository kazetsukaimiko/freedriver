package io.freedriver.serial.stream.api;

import java.io.IOException;
import java.io.OutputStream;

import io.freedriver.base.util.ByteConverter;
import io.freedriver.serial.api.SerialResource;

public class SerialOutputStream extends OutputStream {
    private final SerialResource resource;

    public SerialOutputStream(SerialResource resource) {
        this.resource = resource;
    }

    @Override
    public void write(int i) throws IOException {
        byte[] array = ByteConverter.intToByteArray(i);
        resource.write(new byte[] {array[3]});
    }

    /**
     * {@link OutputStream#close()} is a no-op. Closing this stream must release the
     * shared port, or the next open reports "Port busy".
     */
    @Override
    public void close() throws IOException {
        try {
            resource.close();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Could not close " + resource.getName(), e);
        }
    }
}
