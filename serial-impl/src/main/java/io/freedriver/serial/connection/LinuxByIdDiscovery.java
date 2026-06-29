package io.freedriver.serial.connection;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.freedriver.serial.api.connection.SerialDeviceIdentity;

public final class LinuxByIdDiscovery {
    private static final Path BY_ID_ROOT = Paths.get("/dev/serial/by-id");

    private LinuxByIdDiscovery() {
    }

    public static boolean isSupported() {
        return Files.isDirectory(BY_ID_ROOT);
    }

    public static Stream<SerialDeviceIdentity> discover(Predicate<Path> filter) {
        if (!isSupported()) {
            return Stream.empty();
        }
        try (Stream<Path> links = Files.list(BY_ID_ROOT)) {
            return links
                    .filter(Files::isSymbolicLink)
                    .filter(filter)
                    .map(SerialDeviceIdentity::of);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan " + BY_ID_ROOT, e);
        }
    }

    public static Optional<Path> resolveRealPort(SerialDeviceIdentity identity) {
        Path byIdPath = identity.byIdPath();
        if (!Files.isSymbolicLink(byIdPath)) {
            return Optional.empty();
        }
        try {
            return Optional.of(byIdPath.toRealPath());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}