package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.ByteConverter;
import io.freedriver.serial.api.SerialResource;

import java.io.IOException;
import java.io.OutputStream;

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
}
