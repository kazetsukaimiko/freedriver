package io.freedriver.jsonlink;

import io.freedriver.serial.JSSCSerialResource;
import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.params.SerialParams;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Connectors {
    private static final Logger LOGGER = Logger.getLogger(Connectors.class.getName());
    private static final Set<Connector> ALL_CONNECTORS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<Path, FailedConnector> FAILED_CONNECTORS = new ConcurrentHashMap<>();

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
        LOGGER.info("Creating connector: " + device);
        SerialParams serialParams = new SerialParams();
        //serialParams.setBaudRate(() -> SerialPort.BAUDRATE_115200);
        SerialResource serialResource = new JSSCSerialResource(device, serialParams);
        SerialConnector serialConnector = new SerialConnector(pool, serialResource);
        LOGGER.info("Getting UUID:");
        serialConnector.getUUID();
        ALL_CONNECTORS.add(serialConnector);
        return new ConcurrentConnector(serialConnector);
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

}
