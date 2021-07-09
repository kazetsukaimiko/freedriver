package io.freedriver.serial.stream;

import jssc.SerialPortException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JSSCInputStream extends InputStream {
    private final JSSCPort jsscPort;

    public JSSCInputStream(JSSCPort jsscPort) {
        if (jsscPort.isClosed()) {
            throw new IllegalArgumentException(jsscPort + " port already closed: ");
        }
        this.jsscPort = jsscPort;
    }

    @Override
    public int read() throws IOException {
        try {
            int[] i = jsscPort.getSerialPort().readIntArray(1);
            return i[0];
        } catch (SerialPortException e) {
            throw new IOException("Couldn't read int: ", e);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        jsscPort.close();
    }
}
