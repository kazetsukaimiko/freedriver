package io.freedriver.ee.convention;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppData {
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".config/freedriver");

    public static Path get(String... parts) {
        return Paths.get(CONFIG_PATH.toAbsolutePath().toString(), parts);
    }
}
