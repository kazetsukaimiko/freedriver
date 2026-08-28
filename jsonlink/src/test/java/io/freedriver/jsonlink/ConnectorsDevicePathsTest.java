package io.freedriver.jsonlink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import io.freedriver.jsonlink.config.DeviceByPath;
import org.junit.jupiter.api.Test;

class ConnectorsDevicePathsTest {
    @Test
    void uniqueDevicePathsDedupesCanonical() {
        List<Path> paths = Connectors.uniqueDevicePaths(
                List.of(new DeviceByPath("/dev/ttyACM0"), new DeviceByPath("/dev/./ttyACM0")));
        assertEquals(1, paths.size());
        assertEquals(Path.of("/dev/ttyACM0").toAbsolutePath().normalize(), paths.get(0).normalize());
    }
}
