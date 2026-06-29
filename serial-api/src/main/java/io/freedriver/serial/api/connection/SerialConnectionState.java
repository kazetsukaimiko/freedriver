package io.freedriver.serial.api.connection;

/**
 * Lifecycle states for a managed serial connection.
 *
 * <p>State transitions are emitted via {@link SerialConnectionListener} (per-handle and
 * manager-wide). Register listeners on {@link SerialConnectionHandle} or
 * {@link SerialConnectionManager#addStateListener(SerialConnectionListener)}.
 */
public enum SerialConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING
}