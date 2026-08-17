package io.freedriver.base.util.cache;

import java.time.Duration;

import lombok.Builder;

@Builder(toBuilder = true)
public record CacheSettings(Duration expiry, long maxSize) {

    public CacheSettings(Duration expiry) {
        this(expiry, -1);
    }

    public CacheSettings(long maxSize) {
        this(Duration.ZERO, maxSize);
    }

    public CacheSettings() {
        this(-1);
    }
}
