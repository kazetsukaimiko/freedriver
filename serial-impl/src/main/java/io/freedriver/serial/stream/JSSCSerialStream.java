package io.freedriver.serial.stream;

import io.freedriver.serial.stream.api.SerialStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JSSCSerialStream implements SerialStream {
    private final ExecutorService thread = Executors.newSingleThreadExecutor();
    private final JSSCPort jsscPort;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private Reader reader;
    private Writer writer;

    public JSSCSerialStream(JSSCPort jsscPort) {
        if (jsscPort.isClosed()) {
            throw new IllegalArgumentException(jsscPort + " port already closed: ");
        }
        this.jsscPort = jsscPort;
        this.inputStream = new JSSCInputStream(jsscPort);
        this.outputStream = new JSSCOutputStream(jsscPort);
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing SerialStream");
        jsscPort.close();
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public String getName() {
        return jsscPort.toString();
    }

    @Override
    public boolean isClosed() {
        return jsscPort.isClosed();
    }

}
