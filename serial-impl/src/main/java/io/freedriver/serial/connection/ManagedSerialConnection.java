package io.freedriver.serial.connection;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import io.freedriver.serial.api.SerialResource;
import lombok.extern.java.Log;
import io.freedriver.serial.api.connection.SerialConnectionConfig;
import io.freedriver.serial.api.connection.SerialConnectionHandle;
import io.freedriver.serial.api.connection.SerialConnectionListener;
import io.freedriver.serial.api.connection.SerialConnectionState;
import io.freedriver.serial.api.connection.SerialDeviceIdentity;
import io.freedriver.serial.api.params.SerialParams;

@Log
final class ManagedSerialConnection implements SerialConnectionHandle {
    private final SerialDeviceIdentity identity;
    private final ReconnectingSerialResource resource;
    private final List<SerialConnectionListener> globalListeners;
    private final List<SerialConnectionListener> listeners = new CopyOnWriteArrayList<>();
    private volatile SerialConnectionState state = SerialConnectionState.DISCONNECTED;
    private volatile boolean closed;

    ManagedSerialConnection(
            SerialDeviceIdentity identity,
            SerialParams serialParams,
            SerialConnectionConfig config,
            List<SerialConnectionListener> globalListeners) {
        this.identity = identity;
        this.globalListeners = globalListeners;
        this.resource = new ReconnectingSerialResource(
                identity,
                serialParams,
                () -> LinuxByIdDiscovery.resolveRealPort(identity),
                config.reconnectBackoff(),
                config.indefiniteReadRetry(),
                this::notifyReconnect);
    }

    @Override
    public SerialDeviceIdentity identity() {
        return identity;
    }

    @Override
    public SerialConnectionState state() {
        return state;
    }

    @Override
    public Optional<Path> resolvedPort() {
        return resource.currentPort();
    }

    @Override
    public SerialResource resource() {
        return resource;
    }

    @Override
    public void addListener(SerialConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(SerialConnectionListener listener) {
        listeners.remove(listener);
    }

    void monitor() {
        if (closed) {
            return;
        }
        SerialConnectionState previous = state;
        boolean connected = resource.ensureConnected();
        SerialConnectionState next = connected
                ? SerialConnectionState.CONNECTED
                : SerialConnectionState.DISCONNECTED;
        if (next != previous) {
            transition(previous, next);
        }
    }

    void connectNow() {
        if (closed) {
            return;
        }
        transition(state, SerialConnectionState.CONNECTING);
        boolean connected = resource.ensureConnected();
        transition(
                SerialConnectionState.CONNECTING,
                connected ? SerialConnectionState.CONNECTED : SerialConnectionState.DISCONNECTED);
    }

    private void notifyReconnect() {
        transition(SerialConnectionState.RECONNECTING, SerialConnectionState.CONNECTED);
    }

    private void transition(SerialConnectionState previous, SerialConnectionState next) {
        state = next;
        Path port = resource.currentPort().orElse(null);
        log.fine(() -> identity + " " + previous + " -> " + next + " (" + port + ")");
        emitStateChange(previous, next, port);
    }

    private void emitStateChange(SerialConnectionState previous, SerialConnectionState next, Path port) {
        listeners.forEach(listener -> listener.onStateChanged(identity, previous, next, port));
        globalListeners.forEach(listener -> listener.onStateChanged(identity, previous, next, port));
    }

    @Override
    public void close() {
        closed = true;
        SerialConnectionState previous = state;
        state = SerialConnectionState.DISCONNECTED;
        try {
            resource.close();
        } catch (Exception e) {
            log.warning("Error closing " + identity + ": " + e.getMessage());
        }
        emitStateChange(previous, SerialConnectionState.DISCONNECTED, null);
    }
}