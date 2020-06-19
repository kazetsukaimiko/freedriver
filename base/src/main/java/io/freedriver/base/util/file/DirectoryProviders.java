package io.freedriver.base.util.file;

import java.nio.file.Paths;
import java.util.stream.Stream;

public enum DirectoryProviders {
    TMP(() -> Paths.get("/tmp")),
    HOME(() -> Paths.get(System.getProperty("user.home"))),
    CONFIG(() -> Paths.get(System.getProperty("user.home"),".config"));

    private final DirectoryProvider provider;

    DirectoryProviders(DirectoryProvider provider) {
        this.provider = provider;
    }

    public DirectoryProvider getProvider() {
        return provider;
    }

    public static Stream<DirectoryProviders> stream() {
        return Stream.of(DirectoryProviders.values());
    }
}
