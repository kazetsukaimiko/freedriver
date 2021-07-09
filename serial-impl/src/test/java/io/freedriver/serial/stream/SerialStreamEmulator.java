package io.freedriver.serial.stream;

import io.freedriver.serial.stream.api.SerialStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class SerialStreamEmulator implements SerialStream {
    private List<Integer> fifo = new ArrayList<>();
    private boolean closed = false;

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public InputStream getInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return fifo.remove(0);
            }
        };
    }

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int i) throws IOException {
                fifo.add(i);
            }
        };
    }

    @Override
    public String getName() {
        return "Emulator";
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
