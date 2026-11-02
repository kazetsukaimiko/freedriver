package io.freedriver.metrics;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * Delegates to another emitter. A failed call is queued and resent on the next emit or {@link
 * #flush()}.
 */
public final class QueueingMetricsEmitter implements MetricsEmitter {
    private final MetricsEmitter delegate;
    private final Queue<QueuedCall> pending = new ArrayDeque<>();
    private final Object lock = new Object();

    public QueueingMetricsEmitter(MetricsEmitter delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public void increment(String key, Map<String, Object> tags) {
        emit(MetricsKind.INCREMENT, key, tags);
    }

    @Override
    public void count(String key, Map<String, Object> tags) {
        emit(MetricsKind.COUNT, key, tags);
    }

    @Override
    public void gauge(String key, Map<String, Object> tags) {
        emit(MetricsKind.GAUGE, key, tags);
    }

    /**
     * Resend queued calls in order. Stops at the first remaining failure.
     */
    public void flush() {
        synchronized (lock) {
            flushLocked();
        }
    }

    public int queued() {
        synchronized (lock) {
            return pending.size();
        }
    }

    private void emit(MetricsKind kind, String key, Map<String, Object> tags) {
        Map<String, Object> snapshot = tags == null ? Map.of() : Map.copyOf(tags);
        synchronized (lock) {
            flushLocked();
            try {
                dispatch(kind, key, snapshot);
            } catch (RuntimeException e) {
                pending.add(new QueuedCall(kind, key, snapshot));
            }
        }
    }

    private void flushLocked() {
        while (!pending.isEmpty()) {
            QueuedCall call = pending.peek();
            try {
                dispatch(call.kind, call.key, call.tags);
                pending.poll();
            } catch (RuntimeException e) {
                return;
            }
        }
    }

    private void dispatch(MetricsKind kind, String key, Map<String, Object> tags) {
        switch (kind) {
            case INCREMENT -> delegate.increment(key, tags);
            case COUNT -> delegate.count(key, tags);
            case GAUGE -> delegate.gauge(key, tags);
        }
    }

    private record QueuedCall(MetricsKind kind, String key, Map<String, Object> tags) {}
}
