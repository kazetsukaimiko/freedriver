package io.freedriver.serial.stream.api;

import java.io.BufferedReader;
import java.io.Writer;
import java.nio.charset.Charset;

public interface SerialStreamReader extends SerialStream {
    // TODO: Move these methods here?
    // BufferedReader getReader(Charset charset);
    // Writer getWriter(Charset charset);
}
