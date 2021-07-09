package io.freedriver.daly.bms.stream;

import io.freedriver.serial.stream.api.SerialStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FakeSerialStream implements SerialStream {
    private final byte[] response;
    private final InputStream inputStream;
    private boolean closed = false;

    public FakeSerialStream(byte[] response) {
        this.response = response;
        this.inputStream = new ByteArrayInputStream(response);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public String getName() {
        return "FAKENESS";
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
