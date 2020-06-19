package io.freedriver.base.util.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

public class TempFile implements AutoCloseable {
    private final InputStream inputStream;
    private Path path;

    public TempFile(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        writeInputStreamToFile(inputStream, findAvailablePath());
    }

    public TempFile(Supplier<InputStream> supplier) throws IOException {
        this(supplier.get());
    }

    public Path getPath() {
        return path;
    }

    @Override
    public void close() throws IOException {
        if (path != null) {
            Files.deleteIfExists(path);
        }
    }

    // TODO: Platforms concerns
    private Path findAvailablePath() throws IOException {
        return DirectoryProviders.TMP
                .getProvider()
                .file(UUID.randomUUID().toString())
                .getFile()
                .orElseThrow(() -> new UnsupportedOperationException("Could not find a temp file path to write to"));
    }

    private void writeInputStreamToFile(InputStream inputStream, Path path) throws IOException {
        if (path != null) {
            Files.deleteIfExists(path);
        }
        this.path = path;
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
