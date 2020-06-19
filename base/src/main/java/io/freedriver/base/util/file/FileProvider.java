package io.freedriver.base.util.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;

public interface FileProvider extends PathProvider {
    default Optional<Path> getFile(FileAttribute<?>... attributes) throws IOException {
        Path actualPath = get();
        if (!Files.exists(actualPath)) {
            Files.createFile(actualPath, attributes);
        }
        return PathProvider.ifExists(actualPath);
    }

    default DirectoryProvider getDirectory() {
        return get()::getParent;
    }
}
