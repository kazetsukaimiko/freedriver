package io.freedriver.ee.testing;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Captures {@code io.freedriver.ee} log messages and {@link System#err} for one test.
 */
public final class EeLogCapture implements AutoCloseable {
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    private final PrintStream previousErr;
    private final List<String> lines = new ArrayList<>();
    private final Logger logger;
    private final Level previousLevel;
    private final Handler handler;

    public EeLogCapture() {
        previousErr = System.err;
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
        logger = Logger.getLogger("io.freedriver.ee");
        previousLevel = logger.getLevel();
        logger.setLevel(Level.ALL);
        handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getMessage() != null) {
                    lines.add(record.getMessage());
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
    }

    public void assertAbsent(String secret) {
        if (text().contains(secret)) {
            throw new AssertionError("credential value appeared in ee logs or stderr");
        }
    }

    public void assertContains(String expected) {
        if (!text().contains(expected)) {
            throw new AssertionError("expected log text was absent");
        }
    }

    private String text() {
        return err.toString(StandardCharsets.UTF_8) + "\n" + String.join("\n", lines);
    }

    @Override
    public void close() {
        logger.removeHandler(handler);
        logger.setLevel(previousLevel);
        System.setErr(previousErr);
    }
}
