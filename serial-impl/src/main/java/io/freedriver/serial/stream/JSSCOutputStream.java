package io.freedriver.serial.stream;

import jssc.SerialPortException;

import java.io.IOException;
import java.io.OutputStream;

public class JSSCOutputStream extends OutputStream {
    private final JSSCPort jsscPort;

    public JSSCOutputStream(JSSCPort jsscPort) {
        if (jsscPort.isClosed()) {
            throw new IllegalArgumentException(jsscPort + " port already closed: ");
        }
        this.jsscPort = jsscPort;
    }

    @Override
    public void write(int i) throws IOException {
        try {
            jsscPort.getSerialPort().writeInt(i);
        } catch (SerialPortException e) {
            throw new IOException("Couldn't write int: ", e);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        jsscPort.close();
    }
}
