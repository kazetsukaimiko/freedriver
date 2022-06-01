package io.freedriver.base.util.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface DirectoryProvider extends PathProvider {
    default Optional<Path> getDirectory() throws IOException {
        Path actualPath = get();
        Files.createDirectories(actualPath);
        return PathProvider.ifExists(actualPath);
    }

    default DirectoryProvider subdir(String next) {
        return ()-> get().resolve(next).toAbsolutePath();
    }

    default DirectoryProvider createIfNeeded() throws IOException {
        Files.createDirectories(get());
        return this;
    }

    default FileProvider file(String next) {
        return ()-> get().resolve(next);
    }

    default Stream<FileProvider> files(Predicate<Path> filter) throws IOException {
        return Files.list(get())
                .filter(filter)
                .map(path -> () -> get().resolve(path).toAbsolutePath());
    }
}
