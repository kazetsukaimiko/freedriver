package io.freedriver.base.util.accumulator;

import java.nio.charset.StandardCharsets;

public class NewlineAccumulator extends CharDelimiterAccumulator {
    public static final NewlineAccumulator INSTANCE = new NewlineAccumulator();
    private static final char DELIMITER = '\n';

    public NewlineAccumulator() {
        super(DELIMITER, StandardCharsets.UTF_8);
    }

}
