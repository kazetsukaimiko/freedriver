package io.freedriver.base.util;

import io.freedriver.base.util.accumulator.NewlineAccumulator;

import java.io.InputStream;
import java.util.stream.Stream;

public class ProcessUtil {
    private ProcessUtil() {
        // Prevent construction
    }

    public static Stream<String> linesInputStream(InputStream inputStream) {
        return new EntityStream<>(inputStream, NewlineAccumulator.INSTANCE).stream();
    }

    /*
    public static String readFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        return builder.toString().trim();
    }

     */
}
