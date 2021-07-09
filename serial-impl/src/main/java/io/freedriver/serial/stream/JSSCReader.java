package io.freedriver.serial.stream;

import jssc.SerialPortException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

public class JSSCReader extends Reader {
    private final JSSCPort jsscPort;
    private final Charset charset;
    private int pos = 0;

    public JSSCReader(JSSCPort jsscPort, Charset charset) {
        if (jsscPort.isClosed()) {
            throw new IllegalArgumentException(jsscPort + " port already closed: ");
        }
        this.jsscPort = jsscPort;
        this.charset = charset;
    }

    public int read(char[] b, int offset, int length) throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
