package io.freedriver.clients.ipb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class IPBKey {
    private static final Path PATH = Paths.get(System.getProperty("user.home"), ".ipbkey");

    public static Optional<String> get() {
        try {
            return Optional.of(Files.readString(PATH))
                    .map(String::trim);
        } catch (IOException e) {
            e.printStackTrace(); // TODO log
            return Optional.empty();
        }
    }
}
