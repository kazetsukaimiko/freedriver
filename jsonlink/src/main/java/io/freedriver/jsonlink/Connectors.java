package io.freedriver.jsonlink;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.freedriver.jsonlink.config.ConnectorConfig;
import io.freedriver.jsonlink.config.DevicePathSupplier;
import io.freedriver.serial.SerialRuntime;
import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.SerialResourceFactory;
import io.freedriver.serial.api.params.SerialParams;

public final class Connectors {
    private static final Logger LOGGER = Logger.getLogger(Connectors.class.getName());
    private static final Set<Connector> ALL_CONNECTORS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<Path, FailedConnector> FAILED_CONNECTORS = new ConcurrentHashMap<>();
    private static final Map<String, List<Path>> LAST_EXPANSIONS = new ConcurrentHashMap<>();

    private Connectors() {
        // Prevent Construction
    }

    private static synchronized <T> T connectors(Function<Stream<Connector>, T> setFunction) {
        return setFunction.apply(new HashSet<>(ALL_CONNECTORS).stream());
    }

    private static synchronized Optional<Connector> findByDeviceId(Path device) {
        return connectors(connectors -> connectors
                .filter(connector -> Objects.equals(device, connector.devicePath())))
                .findFirst();
    }

    private static synchronized Connector createConnector(ExecutorService pool, Path device) {
        SerialRuntime.ensureInstalled();
        return openConnector(
                pool,
                device,
                path -> SerialResourceFactory.Holder.create(path, new SerialParams()),
                HandshakeRetry.defaults(),
                Connectors::sleep);
    }

    /**
     * Opens the port and reads the board UUID. Arduino auto-reset starts when the
     * port opens, so each attempt waits {@link HandshakeRetry#retryDelay()} before
     * the handshake. A failed attempt closes the port before the next try.
     */
    static synchronized Connector openConnector(
            ExecutorService pool,
            Path device,
            Function<Path, SerialResource> resources,
            HandshakeRetry retry,
            Consumer<Duration> pause) {
        Exception lastFailure = null;
        for (int attempt = 1; attempt <= retry.maxAttempts(); attempt++) {
            SerialResource serialResource = null;
            SerialConnector serialConnector = null;
            try {
                LOGGER.info("Creating connector: " + device + " (attempt " + attempt + "/" + retry.maxAttempts() + ")");
                serialResource = resources.apply(device);
                serialConnector = new SerialConnector(pool, serialResource);
                if (!retry.retryDelay().isZero()) {
                    LOGGER.info("Waiting " + retry.retryDelay().toMillis() + "ms for " + device + " to finish reset");
                }
                pause.accept(retry.retryDelay());
                LOGGER.info("Getting UUID from " + device);
                UUID uuid = serialConnector.getUUID();
                LOGGER.info("Got UUID " + uuid + " from " + device);
                ALL_CONNECTORS.add(serialConnector);
                return new ConcurrentConnector(serialConnector);
            } catch (Exception failure) {
                lastFailure = failure;
                LOGGER.log(
                        Level.WARNING,
                        "Getting UUID from " + device + " failed on attempt " + attempt + "/"
                                + retry.maxAttempts() + " (" + describe(failure) + "), releasing serial port",
                        failure);
                release(serialConnector, serialResource, failure);
                if (interrupted(failure)) {
                    Thread.currentThread().interrupt();
                    throw new ConnectorException("UUID handshake interrupted for " + device, failure);
                }
            }
        }
        LOGGER.warning(
                "UUID handshake failed for " + device + " after " + retry.maxAttempts()
                        + " attempts; serial port is closed");
        throw new ConnectorException(
                "UUID handshake failed for " + device + " after " + retry.maxAttempts() + " attempts",
                lastFailure);
    }

    static synchronized void resetForTests() {
        List<Connector> open = new ArrayList<>(ALL_CONNECTORS);
        ALL_CONNECTORS.clear();
        FAILED_CONNECTORS.clear();
        for (Connector connector : open) {
            try {
                connector.close();
            } catch (Exception ignored) {
                // Tests only need the static registry empty and the port released.
            }
        }
    }

    private static void release(SerialConnector serialConnector, SerialResource serialResource, Exception failure) {
        try {
            if (serialConnector != null) {
                serialConnector.close();
                return;
            }
            if (serialResource != null) {
                serialResource.close();
            }
        } catch (Exception close) {
            failure.addSuppressed(close);
        }
    }

    private static void sleep(Duration delay) {
        if (delay == null || delay.isZero() || delay.isNegative()) {
            return;
        }
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectorException("Interrupted while waiting for the board to finish reset", e);
        }
    }

    private static boolean interrupted(Throwable failure) {
        for (Throwable current = failure; current != null; current = current.getCause()) {
            if (current instanceof InterruptedException) {
                return true;
            }
        }
        return Thread.currentThread().isInterrupted();
    }

    private static String describe(Throwable failure) {
        Throwable root = failure;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String message = root.getMessage();
        if (message == null || message.isBlank()) {
            message = failure.getMessage();
        }
        return message == null || message.isBlank() ? failure.getClass().getSimpleName() : message;
    }

    public static synchronized Map<Path, FailedConnector> getFailedConnectors() {
        Set<Path> toRemove = FAILED_CONNECTORS.keySet()
                .stream()
                .filter(device -> FAILED_CONNECTORS.get(device).failureExpired())
                .collect(Collectors.toSet());
        toRemove.forEach(FAILED_CONNECTORS::remove);
        return FAILED_CONNECTORS;
    }

    public static synchronized Optional<Connector> findOrOpen(ExecutorService pool, Path device) {
        Optional<Connector> found = findByDeviceId(device);
        if (found.isPresent()) {
            LOGGER.info("Found existing Connector device: " + device);
            Connector inQuestion = found.get();
            if (inQuestion.isClosed()) {
                ALL_CONNECTORS.remove(inQuestion);
            } else {
                return found;
            }
        } else {
            LOGGER.info("No existing Connector device: " + device);
        }
        if (!getFailedConnectors().containsKey(device)) {
            return Optional.of(createConnector(pool, device));
        }
        LOGGER.info("Connector device " + device + " in failed state!");
        return Optional.empty();
    }

    public static synchronized CompletableFuture<Optional<Connector>> findOrOpenAsync(
            Path device, ExecutorService pool) {
        return CompletableFuture
                .supplyAsync(() -> findOrOpen(pool, device), pool);
    }

    public static synchronized CompletableFuture<Void> findOrOpenAndConsume(
            Path device, ExecutorService pool, Consumer<Connector> onCompletion) {
        return findOrOpenAsync(device, pool)
                .thenAccept(optional -> optional.ifPresent(onCompletion));
    }

    public static Optional<Connector> getConnector(UUID deviceId) {
        return connectors(cs -> cs.filter(connector -> Objects.equals(connector.getUUID(), deviceId)))
                .findFirst();
    }

    public static List<Path> allDevices() {
        return uniqueDevicePaths(ConnectorConfig.load().devices());
    }

    static List<Path> uniqueDevicePaths(List<DevicePathSupplier> suppliers) {
        LinkedHashMap<Path, Path> unique = new LinkedHashMap<>();
        for (DevicePathSupplier supplier : suppliers) {
            List<Path> expanded = List.copyOf(supplier.get());
            logExpansion(supplier, expanded);
            for (Path path : expanded) {
                Path canonicalPath = canonical(path);
                Path previous = unique.putIfAbsent(canonicalPath, path.toAbsolutePath());
                if (previous != null) {
                    LOGGER.fine("Skipping duplicate device path " + path + " (same as " + previous + ")");
                }
            }
        }
        List<Path> discovered = List.copyOf(unique.values());
        LOGGER.fine("Discovery unique device paths: " + discovered);
        return discovered;
    }

    private static void logExpansion(DevicePathSupplier supplier, List<Path> expanded) {
        String key = supplier.getClass().getSimpleName() + supplier;
        List<Path> previous = LAST_EXPANSIONS.put(key, expanded);
        if (Objects.equals(previous, expanded)) {
            LOGGER.fine(() -> describeSupplier(supplier) + " expanded to paths " + expanded);
            return;
        }
        LOGGER.info(describeSupplier(supplier) + " expanded to paths " + expanded);
        if (expanded.isEmpty()) {
            LOGGER.warning(describeSupplier(supplier) + " matched no serial device nodes");
        }
    }

    private static String describeSupplier(DevicePathSupplier supplier) {
        return "DevicePathSupplier " + supplier;
    }

    private static Path canonical(Path path) {
        try {
            return path.toRealPath();
        } catch (IOException e) {
            return path.toAbsolutePath().normalize();
        }
    }

}
