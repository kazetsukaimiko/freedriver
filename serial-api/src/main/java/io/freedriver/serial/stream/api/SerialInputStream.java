package io.freedriver.serial.stream.api;

import java.io.IOException;
import java.io.InputStream;

import io.freedriver.base.util.ByteConverter;
import io.freedriver.serial.api.SerialResource;

public class SerialInputStream extends InputStream {
    private final SerialResource resource;

    public SerialInputStream(SerialResource resource) {
        this.resource = resource;
    }

    @Override
    public int read() throws IOException {
        byte[] array = resource.read(1);
        return ByteConverter.byteArrayToInt(new byte[] {0x00, 0x00, 0x00, array[0]});
    }

    /**
     * {@link InputStream#close()} is a no-op. Closing this stream must release the
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
