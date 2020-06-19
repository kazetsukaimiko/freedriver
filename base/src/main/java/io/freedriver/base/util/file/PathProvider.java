package io.freedriver.base.util.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@FunctionalInterface
public interface PathProvider {
    Path get();

    static Optional<Path> ifExists(Path path) {
        if (Files.exists(path)) {
            return Optional.of(path);
        }
        return Optional.empty();
    }
}
