package io.freedriver.base.util.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public interface DirectoryProvider extends PathProvider {
    default Optional<Path> getDirectory() throws IOException {
        Path actualPath = get();
        Files.createDirectories(actualPath);
        return PathProvider.ifExists(actualPath);
    }

    default DirectoryProvider subdir(String next) {
        return ()-> Paths.get(get().toAbsolutePath().toString(), next);
    }

    default FileProvider file(String next) {
        return ()-> Paths.get(get().toAbsolutePath().toString(), next);
    }
}
