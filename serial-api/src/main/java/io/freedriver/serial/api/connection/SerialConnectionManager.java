package io.freedriver.serial.api.connection;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.freedriver.serial.api.params.SerialParams;

/**
 * Owns serial port lifecycle: discovery by stable identity, transparent reconnect, and tty renumber handling.
 */
public interface SerialConnectionManager extends AutoCloseable {
    void start();

    void stop();

    Stream<SerialDeviceIdentity> discover(Predicate<Path> filter);

    SerialConnectionHandle connect(Path byIdPath, SerialParams params);

    SerialConnectionHandle connect(SerialDeviceIdentity identity, SerialParams params);

    Stream<SerialConnectionHandle> activeConnections();

    @Override
    void close();
}