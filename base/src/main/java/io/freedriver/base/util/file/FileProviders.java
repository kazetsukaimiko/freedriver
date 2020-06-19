package io.freedriver.base.util.file;

import java.util.UUID;
import java.util.stream.Stream;

// TODO : Use Platforms to select each, not Locations.
public enum FileProviders {
    // Linux, Unix, etc.
    MAPPINGS(DirectoryProviders.CONFIG.getProvider().file("mappings.json")),
    TEMP_FILE(DirectoryProviders.TMP.getProvider().file(UUID.randomUUID().toString()));

    private final FileProvider provider;

    FileProviders(FileProvider fileProvider) {
        this.provider = fileProvider;
    }

    public FileProvider getProvider() {
        return provider;
    }

    public static Stream<FileProviders> stream() {
        return Stream.of(FileProviders.values());
    }

    @FunctionalInterface
    private interface UnsafeConsumer<A> {
        void apply(A a) throws Exception;
    }
}
