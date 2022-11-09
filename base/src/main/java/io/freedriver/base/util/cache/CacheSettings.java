package io.freedriver.base.util.cache;

import java.time.Duration;

public class CacheSettings {
    private final Duration expiry;
    private final long maxSize;

    public CacheSettings(Duration expiry, long maxSize) {
        this.expiry = expiry;
        this.maxSize = maxSize;
    }

    public CacheSettings(Duration expiry) {
        this(expiry, -1);
    }

    public CacheSettings(long maxSize) {
        this(Duration.ZERO, maxSize);
    }

    public CacheSettings() {
        this(-1);
    }

    public Duration getExpiry() {
        return expiry;
    }

    public long getMaxSize() {
        return maxSize;
    }
}
