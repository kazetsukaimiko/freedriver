package io.freedriver.jsonlink.config.v3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.freedriver.jsonlink.config.ConfigMapper;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import org.junit.jupiter.api.Test;

class MappingsLoadTest {
    private static final String TOGGLE_JSON = "[\"alpha\",\"beta\"]\n";
    private static final String APPLIANCE_JSON = """
            {
              "connectorId": "11111111-1111-1111-1111-111111111111",
              "identifier": 7,
              "groups": ["lights", "pump"]
            }
            """;
    private static final UUID CONNECTOR_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void loadsToggleActionFileWhoseNameEndsWithJson() throws IOException {
        Path home = Files.createTempDirectory("mappings-load-");
        try {
            Path mappings = home.resolve(".config").resolve("jsonlink").resolve("mappings");
            Files.createDirectories(mappings);
            writePair(mappings, TOGGLE_JSON);
            Path nullJson = mappings.resolve("null.json");
            Path badJson = mappings.resolve("bad.json");
            Files.writeString(nullJson, "null");
            Files.writeString(badJson, "{");

            List<LogRecord> warnings = new ArrayList<>();
            Mappings loaded = load(home, warnings);

            assertEquals(
                    List.of(new ToggleAction("alpha"), new ToggleAction("beta")),
                    loaded.toggleActions().get(new EventDescriptor("x.json")));
            assertEquals(1, loaded.toggleActions().size());
            assertTrue(loaded.appliances().isEmpty());
            assertWarning(warnings, nullJson);
            LogRecord bad = assertWarning(warnings, badJson);
            assertInstanceOf(JsonProcessingException.class, bad.getThrown());
        } finally {
            deleteTree(home);
        }
    }

    @Test
    void skipsToggleActionDirectoryNamedJson() throws IOException {
        Path home = Files.createTempDirectory("mappings-load-");
        try {
            Path mappings = home.resolve(".config").resolve("jsonlink").resolve("mappings");
            Files.createDirectories(mappings.resolve("x.json"));

            Mappings loaded = load(home, new ArrayList<>());

            assertTrue(loaded.toggleActions().isEmpty());
        } finally {
            deleteTree(home);
        }
    }

    @Test
    void loadsApplianceFileWhoseNameEndsWithJson() throws IOException {
        Path home = Files.createTempDirectory("mappings-load-");
        try {
            Path appliances = home.resolve(".config").resolve("jsonlink").resolve("mappings").resolve("appliances");
            Files.createDirectories(appliances);
            writePair(appliances, APPLIANCE_JSON);
            Path nullJson = appliances.resolve("null.json");
            Path badJson = appliances.resolve("bad.json");
            Files.writeString(nullJson, "null");
            Files.writeString(badJson, "{");

            List<LogRecord> warnings = new ArrayList<>();
            Mappings loaded = load(home, warnings);

            assertEquals(
                    new Appliance(CONNECTOR_ID, Identifier.of(7), Set.of("lights", "pump")),
                    loaded.appliances().get(new ApplianceDescriptor("x.json")));
            assertEquals(1, loaded.appliances().size());
            assertTrue(loaded.toggleActions().isEmpty());
            assertWarning(warnings, nullJson);
            LogRecord bad = assertWarning(warnings, badJson);
            assertInstanceOf(JsonProcessingException.class, bad.getThrown());
        } finally {
            deleteTree(home);
        }
    }

    @Test
    void skipsApplianceDirectoryNamedJson() throws IOException {
        Path home = Files.createTempDirectory("mappings-load-");
        try {
            Path appliances = home.resolve(".config").resolve("jsonlink").resolve("mappings").resolve("appliances");
            Files.createDirectories(appliances.resolve("x.json"));

            Mappings loaded = load(home, new ArrayList<>());

            assertTrue(loaded.appliances().isEmpty());
        } finally {
            deleteTree(home);
        }
    }

    @Test
    void restoresUserHomeWhenConfigIsAFile() throws IOException {
        Path home = Files.createTempDirectory("mappings-load-");
        try {
            Files.writeString(home.resolve(".config"), "not-a-directory");
            String previous = System.getProperty("user.home");

            assertThrows(IOException.class, () -> load(home, new ArrayList<>()));

            assertEquals(previous, System.getProperty("user.home"));
        } finally {
            deleteTree(home);
        }
    }

    private static void writePair(Path directory, String payload) throws IOException {
        Files.writeString(directory.resolve("x.json"), payload);
        Files.writeString(directory.resolve("x.json.bak"), payload);
    }

    private static Mappings load(Path home, List<LogRecord> warnings) throws IOException {
        String previous = System.getProperty("user.home");
        Logger logger = ConfigMapper.LOGGER;
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                    warnings.add(record);
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        logger.addHandler(handler);
        try {
            System.setProperty("user.home", home.toAbsolutePath().toString());
            return Mappings.load();
        } finally {
            logger.removeHandler(handler);
            if (previous == null) {
                System.clearProperty("user.home");
            } else {
                System.setProperty("user.home", previous);
            }
        }
    }

    private static LogRecord assertWarning(List<LogRecord> warnings, Path path) {
        String message = "Couldn't load JSON: " + path;
        List<LogRecord> matches = warnings.stream()
                .filter(record -> message.equals(record.getMessage()))
                .filter(record -> record.getLevel() == Level.WARNING)
                .toList();
        assertEquals(1, matches.size(), message);
        return matches.get(0);
    }

    private static void deleteTree(Path root) throws IOException {
        if (Files.notExists(root)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }
}
