package io.freedriver.ee.convention;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.freedriver.ee.config.DataSourceConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AppData {
    private static final Logger LOGGER = Logger.getLogger(AppData.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".config/freedriver");

    public static Path get(String... parts) {
        return Paths.get(CONFIG_PATH.toAbsolutePath().toString(), Stream.of(parts)
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
            .toArray(new String[]{}));
    }

    public static <T> Optional<T> loadAs(Class<T> klazz, Path path) {
        LOGGER.info("Loading " + path.toString());
        try {
            return Optional.of(OBJECT_MAPPER.readValue(path.toFile(), klazz));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Stream<DataSourceConfig> loadAllDataSources(String... parts) {
        Path path = get(parts);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Cannot load " + path.toString() + ", path does not exist");
        }
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Unable to load " + path.toString() + ", not a directory");
        }
        try {
            return StreamSupport.stream(Files.newDirectoryStream(path).spliterator(), false)

                    .filter(candidate -> candidate.toString().endsWith("-ds.json"))
                    .peek(System.out::println)
                    .map(selection -> loadAs(DataSourceConfig.class, selection))
                    .flatMap(Optional::stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSourceConfig loadDataSourceConfig(String... parts) {
        return loadAs(DataSourceConfig.class, preconditions(parts))
                .orElseThrow(() -> new IllegalArgumentException("No such datasource"));
    }

    private static Path preconditions(String... parts) {
        Path path = get(parts);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Cannot load " + path.toString() + ", path does not exist");
        }
        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("Cannot load " + path.toString() + " as it is a directory");
        }
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("Cannot load " + path.toString() + " as it is not readable");
        }
        return path;
    }
}
