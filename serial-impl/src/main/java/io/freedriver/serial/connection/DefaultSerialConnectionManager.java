package io.freedriver.serial.connection;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.freedriver.serial.SerialRuntime;
import lombok.extern.java.Log;
import io.freedriver.serial.api.connection.SerialConnectionConfig;
import io.freedriver.serial.api.connection.SerialConnectionHandle;
import io.freedriver.serial.api.connection.SerialConnectionListener;
import io.freedriver.serial.api.connection.SerialConnectionManager;
import io.freedriver.serial.api.connection.SerialDeviceIdentity;
import io.freedriver.serial.api.params.SerialParams;

@Log
public final class DefaultSerialConnectionManager implements SerialConnectionManager {
    private final SerialConnectionConfig config;
    private final Map<SerialDeviceIdentity, ManagedSerialConnection> connections = new ConcurrentHashMap<>();
    private final List<SerialConnectionListener> stateListeners = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService monitorExecutor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "serial-connection-monitor");
                thread.setDaemon(true);
                return thread;
            });
    private ScheduledFuture<?> monitorTask;

    public DefaultSerialConnectionManager() {
        this(SerialConnectionConfig.defaults());
    }

    public DefaultSerialConnectionManager(SerialConnectionConfig config) {
        SerialRuntime.ensureInstalled();
        this.config = config;
    }

    public static DefaultSerialConnectionManager create() {
        DefaultSerialConnectionManager manager = new DefaultSerialConnectionManager();
        manager.start();
        return manager;
    }

    @Override
    public void start() {
        if (monitorTask != null && !monitorTask.isCancelled()) {
            return;
        }
        monitorTask = monitorExecutor.scheduleAtFixedRate(
                this::monitorConnections,
                0,
                config.monitorInterval().toMillis(),
                TimeUnit.MILLISECONDS);
        log.info("Serial connection monitor started");
    }

    @Override
    public void stop() {
        if (monitorTask != null) {
            monitorTask.cancel(false);
            monitorTask = null;
        }
    }

    @Override
    public Stream<SerialDeviceIdentity> discover(Predicate<Path> filter) {
        return LinuxByIdDiscovery.discover(filter);
    }

    @Override
    public SerialConnectionHandle connect(Path byIdPath, SerialParams params) {
        return connect(SerialDeviceIdentity.of(byIdPath), params);
    }

    @Override
    public SerialConnectionHandle connect(SerialDeviceIdentity identity, SerialParams params) {
        ManagedSerialConnection connection = connections.computeIfAbsent(
                identity,
                id -> new ManagedSerialConnection(id, params, config, stateListeners));
        connection.connectNow();
        return connection;
    }

    @Override
    public Stream<SerialConnectionHandle> activeConnections() {
        return connections.values().stream().map(SerialConnectionHandle.class::cast);
    }

    private void monitorConnections() {
        refreshConnections();
    }

    public void refreshConnections() {
        connections.values().forEach(ManagedSerialConnection::monitor);
    }

    @Override
    public void addStateListener(SerialConnectionListener listener) {
        stateListeners.add(listener);
    }

    @Override
    public void removeStateListener(SerialConnectionListener listener) {
        stateListeners.remove(listener);
    }

    @Override
    public void close() {
        stop();
        connections.values().forEach(ManagedSerialConnection::close);
        connections.clear();
        monitorExecutor.shutdown();
        try {
            if (!monitorExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                monitorExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            monitorExecutor.shutdownNow();
        }
        log.info("Serial connection manager closed");
    }
}