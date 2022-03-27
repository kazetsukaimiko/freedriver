package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.ByteConverter;
import io.freedriver.serial.api.SerialResource;

import java.io.IOException;
import java.io.InputStream;

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
}
