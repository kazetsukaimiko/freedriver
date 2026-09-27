package io.freedriver.jsonlink;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.freedriver.serial.JSSCSerialResource;
import io.freedriver.serial.api.params.SerialParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Opens a real PTY with JSSC. The slave is an OS device: a second {@code open} fails with
 * {@code Port busy} while the first file description is still exclusive.
 */
class UuidHandshakePtyReleaseTest {
    @Test
    @Timeout(30)
    void failedHandshakeLetsTheSameDeviceBeOpenedAgain() throws Exception {
        Process python = new ProcessBuilder("python3", "-c", """
                import os, pty, time
                master, slave = pty.openpty()
                print(os.ttyname(slave), flush=True)
                time.sleep(120)
                """).start();
        ExecutorService pool = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "uuid-handshake-pty");
            thread.setDaemon(true);
            return thread;
        });
        JSSCSerialResource reopened = null;
        try {
            String slaveName = new BufferedReader(new InputStreamReader(python.getInputStream())).readLine();
            Path slave = Path.of(slaveName.trim());
            ConnectorException failure = assertThrows(ConnectorException.class, () -> Connectors.openConnector(
                    pool,
                    slave,
                    path -> new JSSCSerialResource(path, new SerialParams()),
                    new HandshakeRetry(1, Duration.ZERO),
                    delay -> {
                    }));
            assertTrue(failure.getMessage().contains(slave.toString()));
            assertTrue(failure.getMessage().contains("after 1 attempts"));

            reopened = new JSSCSerialResource(slave, new SerialParams());
            assertTrue(reopened.isOpened());
        } finally {
            if (reopened != null) {
                reopened.close();
            }
            Connectors.resetForTests();
            pool.shutdownNow();
            python.destroyForcibly();
        }
    }
}
