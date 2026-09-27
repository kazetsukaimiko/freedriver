package io.freedriver.jsonlink.config;

import java.nio.file.Path;
import java.util.List;

/** Explicit device node, e.g. {@code /dev/ttyACM0}. */
public record DeviceByPath(String path) implements DevicePathSupplier {
    public DeviceByPath {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("path required");
        }
        path = path.trim();
    }

    @Override
    public List<Path> get() {
        return List.of(Path.of(path));
    }
}
