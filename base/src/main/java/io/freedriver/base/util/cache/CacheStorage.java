package io.freedriver.base.util.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CacheStorage<K, V> implements Map<K, V> {
    private final CacheSettings settings;
    private final Map<CacheKey<K>, V> backingMap;

    public CacheStorage(CacheSettings settings, Map<CacheKey<K>, V> backingMap) {
        Objects.requireNonNull(settings);
        Objects.requireNonNull(backingMap);
        this.settings = settings;
        this.backingMap = backingMap;
    }

    public  <E> E map(Function<Map<CacheKey<K>, V>, E> mapEFunction) {
        synchronized (backingMap) {
            if (settings.getExpiry().compareTo(Duration.ZERO) > 0) {
                List<CacheKey<K>> expired = backingMap.keySet()
                        .stream().filter(this::expired)
                        .collect(Collectors.toList());
                expired.forEach(backingMap::remove);
            }
            E e = mapEFunction.apply(backingMap);
            if (settings.getMaxSize() > 0 && backingMap.size() >= settings.getMaxSize()) {
                Optional<CacheKey<K>> oldestKey = backingMap.keySet()
                        .stream().min(CacheKey::compareTo);
                oldestKey.ifPresent(backingMap::remove);
            }
            return e;
        }
    }
    private boolean expired(CacheKey<K> kCacheKey) {
        return Instant.now().compareTo(kCacheKey.getCreated().plus(settings.getExpiry())) >= 0;
    }

    public void using(Consumer<Map<CacheKey<K>, V>> mapConsumer) {
        map(m -> {
            mapConsumer.accept(m);
            return null;
        });
    }
}
