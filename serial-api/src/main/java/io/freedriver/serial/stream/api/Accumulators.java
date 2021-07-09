package io.freedriver.serial.stream.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public final class Accumulators {
    private Accumulators() {
    }

    public static Accumulator<byte[]> delimited(final Character delimiter) {
        return delimited(StandardCharsets.US_ASCII, delimiter);
    }

    public static Accumulator<byte[]> delimited(final Charset charset, final Character delimiter) {
        return stream -> delimited(charset, delimiter, stream);
    }

    /*
    public static byte[] delimited(final Charset charset, final Character delimiter, final SerialStream stream) throws IOException {
        final CharsetDecoder decoder = charset.newDecoder();
        final Reader r = stream.getReader(charset);

        char[] buf = new char[1];
        StringBuilder sb = new StringBuilder();
        while (stream.isOpened()) {
            int read = r.read(buf);
            if (read > 0) {
                sb.append(buf[0]);
                if (buf[0] == delimiter) {
                    System.out.println("Delim");
                    break;
                }
            }
            while (r.ready()) {

            }
        }
        return sb.toString().getBytes(charset);
    }
     */


    // TODO Figure out the buffer problem with InputStreamReader causing delays
    public static byte[] delimited(final Charset charset, final Character delimiter, final SerialStream stream) throws IOException {
        final InputStream is = stream.getInputStream();

        byte[] buf = new byte[1];
        StringBuilder sb = new StringBuilder();
        while (stream.isOpened()) {
            int read = is.read(buf);
            if (read > 0) {
                sb.append(new String(buf, charset));
                if (buf[0] == delimiter) {
                    break;
                }
            }
        }
        return sb.toString().getBytes(charset);
    }

    private static int charCode(String s) {
        byte[] nl = StandardCharsets.US_ASCII.encode(s)
                .array();
        byte nlc = nl[0];
        return nlc < 0 ? nlc + 255 : nlc;
    }

    public static Accumulator<String> delimitedString(final Character delimiter) {
        return delimitedString(StandardCharsets.UTF_8, delimiter);
    }

    public static Accumulator<String> delimitedString(final Charset charset, final Character delimiter) {
        return stream -> new String(delimited(charset, delimiter, stream), charset);
    }

    public static Accumulator<String> linesString() {
        return linesString(StandardCharsets.US_ASCII);
    }

    public static Accumulator<String> linesString(final Charset charset) {
        return stream -> new String(delimited(charset, '\n', stream), charset);
    }
}
