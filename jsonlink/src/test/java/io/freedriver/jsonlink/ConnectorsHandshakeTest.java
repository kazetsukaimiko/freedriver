package io.freedriver.jsonlink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.exception.SerialResourceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectorsHandshakeTest {
    private static final UUID BOARD_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Path DEVICE = Path.of("/dev/ttyACM0");
    private static final Duration RESET_DELAY = Duration.ofSeconds(2);

    private ExecutorService pool;
    private List<String> events;
    private List<Duration> delays;
    private List<FakePort> ports;

    @BeforeEach
    void setUp() {
        pool = Executors.newSingleThreadExecutor();
        events = new ArrayList<>();
        delays = new ArrayList<>();
        ports = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        Connectors.resetForTests();
        pool.shutdownNow();
        OsExclusivePort.releaseAll();
        System.clearProperty("jsonlink.handshake.maxAttempts");
        System.clearProperty("jsonlink.handshake.retryDelay");
    }

    @Test
    void releasesPortAfterOneFailedHandshakeThenSucceeds() {
        Connector connector = open(1, false);

        assertEquals(BOARD_UUID, connector.getUUID());
        assertEquals(
                List.of("open", "delay", "write", "write", "close", "open", "delay", "write"),
                events);
        assertEquals(List.of(RESET_DELAY, RESET_DELAY), delays);
        assertEquals(2, ports.size());
        assertFalse(ports.get(0).isOpened());
        assertTrue(ports.get(1).isOpened());
        assertFalse(events.contains("busy"));
    }

    @Test
    void releasesPortAfterEveryFailedHandshake() {
        List<LogRecord> warnings = new ArrayList<>();
        ConnectorException failure = assertThrows(
                ConnectorException.class,
                () -> withWarnings(warnings, () -> open(Integer.MAX_VALUE, true)));

        String giveUp = "UUID handshake failed for " + DEVICE + " after 3 attempts; serial port is closed";
        List<LogRecord> giveUpLines = warnings.stream()
                .filter(record -> giveUp.equals(record.getMessage()))
                .toList();
        assertEquals(List.of(giveUp), giveUpLines.stream().map(LogRecord::getMessage).toList());
        assertNull(giveUpLines.get(0).getThrown());
        assertEquals("UUID handshake failed for " + DEVICE + " after 3 attempts", failure.getMessage());
        assertEquals(
                List.of(
                        "open", "delay", "write", "close",
                        "open", "delay", "write", "close",
                        "open", "delay", "write", "close"),
                events);
        assertEquals(List.of(RESET_DELAY, RESET_DELAY, RESET_DELAY), delays);
        assertEquals(3, ports.size());
        assertTrue(ports.stream().noneMatch(FakePort::isOpened));
        assertFalse(events.contains("busy"));
        assertFalse(OsExclusivePort.isHeld(DEVICE.toString()));
        OsExclusivePort reopened = OsExclusivePort.tryOpen(DEVICE.toString());
        reopened.release();
    }

    @Test
    void regainsBoardAfterRebootWhenFirstOpensAreStillResetting() {
        int failuresWhileResetting = 2;
        List<Integer> alreadyOpen = new ArrayList<>();
        Consumer<Duration> pause = delay -> {
            delays.add(delay);
            events.add("delay");
        };

        Connector connector = Connectors.openConnector(
                pool,
                DEVICE,
                path -> {
                    alreadyOpen.add((int) ports.stream().filter(FakePort::isOpened).count());
                    return openPort(path, failuresWhileResetting, false);
                },
                new HandshakeRetry(3, RESET_DELAY),
                pause);

        assertFalse(connector.isClosed());
        assertEquals(BOARD_UUID, connector.getUUID());
        assertEquals(DEVICE.toString(), connector.device());
        assertEquals(List.of(0, 0, 0), alreadyOpen);
        assertEquals(1, ports.stream().filter(FakePort::isOpened).count());
        assertFalse(ports.get(0).isOpened());
        assertFalse(ports.get(1).isOpened());
        assertTrue(ports.get(2).isOpened());
        assertFalse(events.contains("busy"));
        assertEquals(
                List.of(
                        "open", "delay", "write", "write", "close",
                        "open", "delay", "write", "write", "close",
                        "open", "delay", "write"),
                events);
        assertEquals(List.of(RESET_DELAY, RESET_DELAY, RESET_DELAY), delays);
    }

    @Test
    void retryDefaultsMatchArduinoReset() {
        System.clearProperty("jsonlink.handshake.maxAttempts");
        System.clearProperty("jsonlink.handshake.retryDelay");

        assertEquals(3, HandshakeRetry.DEFAULT_MAX_ATTEMPTS);
        assertEquals(Duration.ofSeconds(2), HandshakeRetry.DEFAULT_RETRY_DELAY);
        assertEquals(new HandshakeRetry(3, Duration.ofSeconds(2)), HandshakeRetry.defaults());
    }

    @Test
    void retrySettingsComeFromSystemProperties() {
        System.setProperty("jsonlink.handshake.maxAttempts", "5");
        System.setProperty("jsonlink.handshake.retryDelay", "PT4S");
        assertEquals(new HandshakeRetry(5, Duration.ofSeconds(4)), HandshakeRetry.defaults());

        System.setProperty("jsonlink.handshake.retryDelay", "1500");
        assertEquals(Duration.ofMillis(1500), HandshakeRetry.defaults().retryDelay());

        System.setProperty("jsonlink.handshake.retryDelay", "nope");
        assertEquals(HandshakeRetry.DEFAULT_RETRY_DELAY, HandshakeRetry.defaults().retryDelay());

        System.setProperty("jsonlink.handshake.maxAttempts", "0");
        assertEquals(HandshakeRetry.DEFAULT_MAX_ATTEMPTS, HandshakeRetry.defaults().maxAttempts());
    }

    private void withWarnings(List<LogRecord> warnings, Runnable action) {
        Logger logger = Logger.getLogger(Connectors.class.getName());
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                    warnings.add(record);
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        logger.addHandler(handler);
        try {
            action.run();
        } finally {
            logger.removeHandler(handler);
        }
    }

    private Connector open(int failingOpens, boolean throwOnFail) {
        Consumer<Duration> pause = delay -> {
            delays.add(delay);
            events.add("delay");
        };
        return Connectors.openConnector(
                pool,
                DEVICE,
                path -> openPort(path, failingOpens, throwOnFail),
                new HandshakeRetry(3, RESET_DELAY),
                pause);
    }

    private SerialResource openPort(Path path, int failingOpens, boolean throwOnFail) {
        OsExclusivePort device;
        try {
            device = OsExclusivePort.tryOpen(path.toString());
        } catch (SerialResourceException busy) {
            events.add("busy");
            throw busy;
        }
        boolean fail = ports.size() < failingOpens;
        FakePort port = new FakePort(path, fail, throwOnFail, events, device);
        ports.add(port);
        events.add("open");
        return port;
    }

    /**
     * Exclusive open of one device path. A second open throws {@code Port busy} until
     * {@link #release()} — the same rule as JSSC {@code TIOCEXCL}.
     */
    static final class OsExclusivePort {
        private static final ConcurrentMap<String, OsExclusivePort> HELD = new ConcurrentHashMap<>();
        private final String path;

        private OsExclusivePort(String path) {
            this.path = path;
        }

        static OsExclusivePort tryOpen(String path) {
            OsExclusivePort port = new OsExclusivePort(path);
            if (HELD.putIfAbsent(path, port) != null) {
                throw new SerialResourceException("Port busy");
            }
            return port;
        }

        static boolean isHeld(String path) {
            return HELD.containsKey(path);
        }

        static void releaseAll() {
            HELD.clear();
        }

        void release() {
            HELD.remove(path, this);
        }
    }

    private static final class FakePort implements SerialResource {
        private final String name;
        private final boolean fail;
        private final boolean throwOnFail;
        private final List<String> events;
        private final OsExclusivePort device;
        private final Queue<Byte> pending = new ArrayDeque<>();
        private final ByteArrayOutputStream request = new ByteArrayOutputStream();
        private boolean opened = true;

        private FakePort(
                Path path, boolean fail, boolean throwOnFail, List<String> events, OsExclusivePort device) {
            this.name = path.toString();
            this.fail = fail;
            this.throwOnFail = throwOnFail;
            this.events = events;
            this.device = device;
        }

        @Override
        public void write(byte[] array) {
            if (!opened) {
                throw new SerialResourceException("Port busy");
            }
            request.writeBytes(array);
            if (!requestComplete()) {
                return;
            }
            byte[] payload = request.toByteArray();
            request.reset();
            events.add("write");
            if (fail && throwOnFail) {
                throw new SerialResourceException("simulated handshake failure");
            }
            queueResponse(payload);
        }

        private boolean requestComplete() {
            String text = request.toString(StandardCharsets.UTF_8);
            if (text.isEmpty() || text.charAt(0) != '{') {
                return false;
            }
            int depth = 0;
            boolean inString = false;
            boolean escape = false;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (inString) {
                    if (escape) {
                        escape = false;
                    } else if (c == '\\') {
                        escape = true;
                    } else if (c == '"') {
                        inString = false;
                    }
                    continue;
                }
                if (c == '"') {
                    inString = true;
                } else if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        return i == text.length() - 1;
                    }
                }
            }
            return false;
        }

        @Override
        public byte[] read(int size) {
            if (!opened) {
                throw new SerialResourceException("port closed");
            }
            byte[] out = new byte[size];
            for (int i = 0; i < size; i++) {
                Byte next = pending.poll();
                if (next == null) {
                    throw new SerialResourceException("handshake response missing");
                }
                out[i] = next;
            }
            return out;
        }

        @Override
        public void clear() {
            pending.clear();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isOpened() {
            return opened;
        }

        @Override
        public void close() {
            if (!opened) {
                return;
            }
            opened = false;
            device.release();
            events.add("close");
        }

        private void queueResponse(byte[] requestBytes) {
            String requestId;
            try {
                JsonNode node = Connector.MAPPER.readTree(requestBytes);
                requestId = node.path("requestId").asText();
            } catch (Exception e) {
                throw new SerialResourceException("Could not read handshake request", e);
            }
            String body = fail
                    ? "{\"requestId\":\"" + requestId + "\"}\n"
                    : "{\"uuid\":\"" + BOARD_UUID + "\",\"requestId\":\"" + requestId + "\"}\n";
            for (byte b : body.getBytes(StandardCharsets.UTF_8)) {
                pending.add(b);
            }
        }
    }
}
