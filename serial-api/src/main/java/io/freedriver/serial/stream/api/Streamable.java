package io.freedriver.serial.stream.api;

import java.io.IOException;
import java.io.OutputStream;

public interface Streamable {
    void write(OutputStream outputStream) throws IOException;
}
