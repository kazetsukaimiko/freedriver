package io.freedriver.serial.connection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.SerialResourceFactory;
import io.freedriver.serial.api.connection.SerialDeviceIdentity;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.params.SerialParams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReconnectingSerialResourceTest {
    private final AtomicInteger openCount = new AtomicInteger();
    private final AtomicInteger readCount = new AtomicInteger();
    private Path currentPort = Paths.get("/dev/ttyACM0");

    @BeforeEach
    void installFactory() {
        openCount.set(0);
        readCount.set(0);
        SerialResourceFactory.Holder.install((path, params) -> new FakePort(path, openCount, readCount));
    }

    @AfterEach
    void clearFactory() {
        SerialResourceFactory.Holder.install((path, params) -> {
            throw new SerialResourceException("test factory cleared");
        });
    }

    @Test
    void reconnectsWhenResolvedPortChanges() {
        SerialDeviceIdentity identity = SerialDeviceIdentity.of(
                Paths.get("/dev/serial/by-id/usb-Example_Device-if00-port0"));

        ReconnectingSerialResource resource = new ReconnectingSerialResource(
                identity,
                new SerialParams(),
                () -> Optional.of(currentPort),
                Duration.ZERO,
                null);

        assertTrue(resource.ensureConnected());
        assertEquals(Paths.get("/dev/ttyACM0"), resource.currentPort().orElseThrow());
        assertEquals(1, openCount.get());

        currentPort = Paths.get("/dev/ttyACM1");
        resource.forceReconnect();

        assertEquals(Paths.get("/dev/ttyACM1"), resource.currentPort().orElseThrow());
        assertEquals(2, openCount.get());
    }

    @Test
    void retriesReadAfterDelegateFailure() {
        SerialDeviceIdentity identity = SerialDeviceIdentity.of(
                Paths.get("/dev/serial/by-id/usb-Example_Device-if00-port0"));

        ReconnectingSerialResource resource = new ReconnectingSerialResource(
                identity,
                new SerialParams(),
                () -> Optional.of(currentPort),
                Duration.ZERO,
                null);
        resource.ensureConnected();

        assertArrayEquals(new byte[] {1}, resource.read(1));
        assertEquals(2, readCount.get());
    }

    private static final class FakePort implements SerialResource {
        private final Path path;
        private final AtomicInteger openCount;
        private final AtomicInteger readCount;
        private boolean open = true;

        private FakePort(Path path, AtomicInteger openCount, AtomicInteger readCount) {
            this.path = path;
            this.openCount = openCount;
            this.readCount = readCount;
            openCount.incrementAndGet();
        }

        @Override
        public void write(byte[] array) {
        }

        @Override
        public byte[] read(int size) {
            readCount.incrementAndGet();
            if (readCount.get() == 1) {
                throw new SerialResourceException("simulated disconnect");
            }
            return new byte[] {1};
        }

        @Override
        public void clear() {
        }

        @Override
        public String getName() {
            return path.toString();
        }

        @Override
        public boolean isOpened() {
            return open;
        }

        @Override
        public void close() {
            open = false;
        }
    }
}