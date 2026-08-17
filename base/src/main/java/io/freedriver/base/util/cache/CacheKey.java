package io.freedriver.base.util.cache;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Builder;

@Builder(toBuilder = true)
public record CacheKey<K>(long instanceId, Instant created, K key) implements Comparable<CacheKey<K>> {
    private static final AtomicLong instanceCount = new AtomicLong(0);

    public CacheKey(Instant created, K key) {
        this(instanceCount.getAndIncrement(), created, key);
    }

    public CacheKey(K key) {
        this(Instant.now(), key);
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
