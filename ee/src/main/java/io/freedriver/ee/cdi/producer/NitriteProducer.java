package io.freedriver.ee.cdi.producer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.freedriver.ee.cdi.qualifier.NitriteDatabase;
import io.freedriver.ee.cdi.qualifier.NitriteDatabaseLiteral;
import io.freedriver.ee.convention.AppData;
import io.freedriver.ee.prop.DeploymentProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.dizitart.no2.Nitrite;

@ApplicationScoped
public class NitriteProducer {
    private static final Logger LOGGER = Logger.getLogger(NitriteProducer.class.getName());
    static final String CREDENTIALS_FILE = "nitrite-credentials.json";

    private final Map<NitriteDatabase, Nitrite> cache = new ConcurrentHashMap<>(new HashMap<>());

    @Produces
    @NitriteDatabase
    public Nitrite getNitrite(InjectionPoint injectionPoint) {
        NitriteDatabase nitriteDatabase = injectionPoint.getQualifiers()
                .stream()
                .filter(NitriteDatabase.class::isInstance)
                .map(NitriteDatabase.class::cast)
                .findFirst()
                .orElseGet(NitriteDatabaseLiteral::new);
        ResolvedLocations locations = locate(nitriteDatabase);
        return getOrBuild(nitriteDatabase, () -> loadThenOpen(
                locations.directory(),
                locations.store().getFileName().toString()));
    }

    /**
     * Deployment name used for both the credentials directory and the store file.
     * {@code deployment.name} wins over the qualifier, matching {@link #inConfigDirectory(NitriteDatabase)}.
     */
    static String resolveDeployment(NitriteDatabase nitriteDatabase) {
        return DeploymentProperties.NAME.fetch().orElse(nitriteDatabase.deployment());
    }

    static ResolvedLocations locate(NitriteDatabase nitriteDatabase) {
        String deployment = resolveDeployment(nitriteDatabase);
        Path directory = AppData.get(deployment);
        Path store = directory.resolve(getEntityDatabase(nitriteDatabase.database()));
        return new ResolvedLocations(deployment, directory, store);
    }

    static NitriteCredentials loadCredentials(Path deploymentDirectory) {
        Path file = deploymentDirectory.resolve(CREDENTIALS_FILE);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException(CREDENTIALS_FILE + " is missing");
        }
        if (!Files.isReadable(file)) {
            throw new IllegalArgumentException(CREDENTIALS_FILE + " is not readable");
        }
        Optional<JsonNode> loaded = AppData.loadAs(JsonNode.class, file);
        if (loaded.isEmpty() || loaded.get().isNull() || !loaded.get().isObject()) {
            throw new IllegalArgumentException(CREDENTIALS_FILE + " is malformed or has a null root");
        }
        JsonNode root = loaded.get();
        String userId = requireString(root, "userId");
        String password = requireString(root, "password");
        return new NitriteCredentials(userId, password);
    }

    /**
     * Loads credentials, then creates the deployment directory and opens the store.
     * A missing or invalid credentials file fails before the directory or store file is created.
     */
    static Nitrite loadThenOpen(Path deploymentDirectory, String storeFileName) {
        NitriteCredentials credentials = loadCredentials(deploymentDirectory);
        Path store = createStorePath(deploymentDirectory, storeFileName);
        return open(store, credentials);
    }

    /**
     * Opens {@code store} with {@code credentials}. Nitrite opens the MVStore before it checks the
     * password, which writes the file; on failure the previous bytes are put back, and a store that
     * did not exist is removed.
     */
    static Nitrite open(Path store, NitriteCredentials credentials) {
        boolean existed = Files.isRegularFile(store);
        Path backup = null;
        boolean attempted = false;
        try {
            if (existed) {
                backup = Files.createTempFile("nitrite-store-", ".bak");
                Files.copy(store, backup, StandardCopyOption.REPLACE_EXISTING);
            }
            attempted = true;
            return Nitrite.builder()
                    .registerModule(new JavaTimeModule())
                    .compressed()
                    .filePath(store.toFile())
                    .openOrCreate(credentials.userId(), credentials.password());
        } catch (IOException e) {
            if (attempted) {
                undoFailedOpen(store, existed, backup);
            }
            throw new UncheckedIOException(e);
        } catch (RuntimeException e) {
            if (attempted) {
                restoreAfterFailure(store, existed, backup, e);
            }
            throw e;
        } finally {
            if (backup != null) {
                try {
                    Files.deleteIfExists(backup);
                } catch (IOException ignored) {
                    LOGGER.warning("Failed to delete store backup (" + ignored.getClass().getName() + ")");
                }
            }
        }
    }

    private static void undoFailedOpen(Path store, boolean existed, Path backup) {
        try {
            restoreStore(store, existed, backup);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void restoreAfterFailure(Path store, boolean existed, Path backup, RuntimeException failure) {
        try {
            restoreStore(store, existed, backup);
        } catch (IOException e) {
            failure.addSuppressed(new UncheckedIOException(e));
        }
    }

    private static void restoreStore(Path store, boolean existed, Path backup) throws IOException {
        if (existed && backup != null && Files.isRegularFile(backup)) {
            Files.copy(backup, store, StandardCopyOption.REPLACE_EXISTING);
        } else if (!existed) {
            Files.deleteIfExists(store);
        }
    }

    private static String requireString(JsonNode root, String field) {
        if (!root.has(field)) {
            throw new IllegalArgumentException(CREDENTIALS_FILE + " field " + field + " is missing or blank");
        }
        JsonNode node = root.get(field);
        if (!node.isTextual()) {
            throw new IllegalArgumentException(CREDENTIALS_FILE + " field " + field + " is not a string");
        }
        String value = node.textValue();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(CREDENTIALS_FILE + " field " + field + " is missing or blank");
        }
        return value;
    }

    private static Path createStorePath(Path deploymentDirectory, String storeFileName) {
        LOGGER.warning("INConfig: " + deploymentDirectory);
        if (!Files.isDirectory(deploymentDirectory)) {
            try {
                Files.createDirectories(deploymentDirectory);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return deploymentDirectory.resolve(storeFileName);
    }

    public static Path inConfigDirectory(NitriteDatabase nitriteDatabase) {
        return inConfigDirectory(resolveDeployment(nitriteDatabase), nitriteDatabase.database());
    }

    public static Path inConfigDirectory(String deployment, Class<?> entityClass) {
        Path deployPath = AppData.get(deployment);
        LOGGER.warning("INConfig: " + deployPath.toString());
        if (!Files.isDirectory(deployPath)) {
            try {
                Files.createDirectories(deployPath);
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO
            }
        }
        return AppData.get(deployment, getEntityDatabase(entityClass));
    }

    public static String getEntityDatabase(Class<?> klazz) {
        return ((klazz == null || klazz == Object.class) ?
                "Default" : klazz.getSimpleName())
                + ".db";
    }

    public synchronized Nitrite getOrBuild(NitriteDatabase nitriteDatabase, Supplier<Nitrite> supplier) {
        Nitrite resolved =  Optional.of(nitriteDatabase)
                .filter(cache::containsKey)
                .map(cache::get)
                .orElseGet(supplier);
        if (!cache.containsKey(nitriteDatabase)) {
            cache.put(nitriteDatabase, resolved);
        }
        return resolved;
    }

    record ResolvedLocations(String deployment, Path directory, Path store) {
    }
}
