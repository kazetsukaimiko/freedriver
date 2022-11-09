package io.freedriver.base.util.cache;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class CacheKey<K> implements Comparable<CacheKey<K>> {
    private static final AtomicLong instanceCount = new AtomicLong(0);
    private final long instanceId;
    private final Instant created;
    private final K key;

    private CacheKey(long instanceId, Instant created, K key) {
        this.instanceId = instanceId;
        this.created = created;
        this.key = key;
    }

    public CacheKey(Instant created, K key) {
        this(instanceCount.getAndIncrement(), created, key);
    }

    public CacheKey(K key) {
        this(Instant.now(), key);
    }

    public Instant getCreated() {
        return created;
    }

    public K getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey<?> cacheKey = (CacheKey<?>) o;
        return Objects.equals(key, cacheKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public int compareTo(CacheKey<K> kCacheKey) {
        return Long.compare(instanceId, kCacheKey.instanceId);
    }
}
