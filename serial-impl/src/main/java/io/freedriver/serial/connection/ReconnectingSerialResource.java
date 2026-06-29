package io.freedriver.serial.connection;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;

import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.SerialResourceFactory;
import io.freedriver.serial.api.connection.SerialDeviceIdentity;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.params.SerialParams;
import lombok.extern.java.Log;

/**
 * A {@link SerialResource} whose delegate may be swapped when the resolved tty path changes or I/O fails.
 */
@Log
public class ReconnectingSerialResource implements SerialResource {
    private final SerialDeviceIdentity identity;
    private final SerialParams serialParams;
    private final Supplier<Optional<Path>> portResolver;
    private final Duration reconnectBackoff;
    private final boolean indefiniteReadRetry;
    private final Runnable onReconnect;
    private final Object lock = new Object();

    private SerialResource delegate;
    private Path currentPort;
    private boolean closed;

    public ReconnectingSerialResource(
            SerialDeviceIdentity identity,
            SerialParams serialParams,
            Supplier<Optional<Path>> portResolver,
            Duration reconnectBackoff,
            boolean indefiniteReadRetry,
            Runnable onReconnect) {
        this.identity = Objects.requireNonNull(identity, "identity");
        this.serialParams = Objects.requireNonNull(serialParams, "serialParams");
        this.portResolver = Objects.requireNonNull(portResolver, "portResolver");
        this.reconnectBackoff = Objects.requireNonNull(reconnectBackoff, "reconnectBackoff");
        this.indefiniteReadRetry = indefiniteReadRetry;
        this.onReconnect = onReconnect == null ? () -> {} : onReconnect;
    }

    public ReconnectingSerialResource(
            SerialDeviceIdentity identity,
            SerialParams serialParams,
            Supplier<Optional<Path>> portResolver,
            Duration reconnectBackoff,
            Runnable onReconnect) {
        this(identity, serialParams, portResolver, reconnectBackoff, true, onReconnect);
    }

    public SerialDeviceIdentity identity() {
        return identity;
    }

    public Optional<Path> currentPort() {
        synchronized (lock) {
            return Optional.ofNullable(currentPort);
        }
    }

    public boolean ensureConnected() {
        synchronized (lock) {
            if (closed) {
                return false;
            }
            Optional<Path> resolved = portResolver.get();
            if (resolved.isEmpty()) {
                closeDelegate();
                currentPort = null;
                return false;
            }
            Path target = resolved.get();
            if (delegate != null && delegate.isOpened() && Objects.equals(currentPort, target)) {
                return true;
            }
            reconnectTo(target);
            return delegate != null && delegate.isOpened();
        }
    }

    public void forceReconnect() {
        synchronized (lock) {
            if (closed) {
                return;
            }
            closeDelegate();
            currentPort = null;
            ensureConnected();
        }
    }

    private void reconnectTo(Path target) {
        closeDelegate();
        currentPort = target;
        try {
            delegate = SerialResourceFactory.Holder.create(target, serialParams);
            onReconnect.run();
            log.info(() -> "Connected " + identity + " on " + target);
        } catch (RuntimeException e) {
            closeDelegate();
            currentPort = null;
            log.log(Level.WARNING, "Failed to open " + identity + " on " + target, e);
        }
    }

    private void closeDelegate() {
        if (delegate == null) {
            return;
        }
        try {
            delegate.close();
        } catch (Exception e) {
            log.log(Level.FINE, "Error closing delegate for " + identity, e);
        } finally {
            delegate = null;
        }
    }

    private SerialResource requireDelegate() {
        if (!ensureConnected()) {
            throw new SerialResourceException("Serial device unavailable: " + identity);
        }
        return delegate;
    }

    private <T> T withDelegate(RetryingOperation<T> operation, int maxAttempts) {
        int attempts = 0;
        while (attempts < maxAttempts) {
            attempts++;
            try {
                return operation.apply(requireDelegate());
            } catch (SerialResourceException e) {
                log.log(Level.WARNING, "I/O failure on " + identity + ", reconnecting", e);
                synchronized (lock) {
                    closeDelegate();
                    currentPort = null;
                }
                backoff();
            }
        }
        throw new SerialResourceException("Serial I/O failed after reconnect: " + identity);
    }

    private void backoff() {
        if (reconnectBackoff.isZero() || reconnectBackoff.isNegative()) {
            return;
        }
        try {
            Thread.sleep(reconnectBackoff.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public byte[] read(int size) {
        if (indefiniteReadRetry) {
            return readUntilSuccess(size);
        }
        return withDelegate(delegate -> delegate.read(size), 2);
    }

    @Override
    public void write(byte[] array) {
        withDelegate(delegate -> {
            delegate.write(array);
            return null;
        }, 2);
    }

    @Override
    public void clear() {
        withDelegate(delegate -> {
            delegate.clear();
            return null;
        }, 2);
    }

    private byte[] readUntilSuccess(int size) {
        while (!closed) {
            try {
                return withDelegate(delegate -> delegate.read(size), 1);
            } catch (SerialResourceException e) {
                log.log(Level.FINE, "Read waiting for reconnect on " + identity, e);
                backoff();
            }
        }
        throw new SerialResourceException("Serial resource closed: " + identity);
    }

    @Override
    public String getName() {
        synchronized (lock) {
            if (currentPort != null) {
                return currentPort.toString();
            }
        }
        return identity.byIdPath().toString();
    }

    @Override
    public boolean isOpened() {
        synchronized (lock) {
            return !closed && delegate != null && delegate.isOpened();
        }
    }

    @Override
    public void close() {
        synchronized (lock) {
            closed = true;
            closeDelegate();
            currentPort = null;
        }
    }

    @FunctionalInterface
    private interface RetryingOperation<T> {
        T apply(SerialResource delegate);
    }
}