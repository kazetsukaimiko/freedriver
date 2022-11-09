package io.freedriver.base.util.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Cache<K, V> extends CacheStorage<K, V> {

    public Cache(CacheSettings settings, Map<CacheKey<K>, V> backingMap) {
        super(settings, backingMap);
    }

    public Cache(CacheSettings settings) {
        this(settings, new ConcurrentHashMap<>());
    }

    public Cache() {
        this(new CacheSettings());
    }

    @Override
    public int size() {
        return map(Map::size);
    }

    @Override
    public boolean isEmpty() {
        return map(Map::isEmpty);
    }

    @Override
    public boolean containsKey(Object o) {
        return map(map -> map.containsKey(new CacheKey<>(o)));
    }

    @Override
    public boolean containsValue(Object o) {
        return map(map -> map.containsValue(o));
    }

    @Override
    public V get(Object o) {
        return map(map -> map.get(new CacheKey<>(o)));
    }

    @Override
    public V put(K k, V v) {
        return map(map -> map.put(new CacheKey<>(k), v));
    }

    @Override
    public V remove(Object o) {
        return map(map -> map.remove(new CacheKey<>(o)));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        using(m -> map.forEach((k, v) -> m.put(new CacheKey<>(k), v)));
    }

    @Override
    public void clear() {
        using(Map::clear);
    }

    @Override
    public Set<K> keySet() {
        return map(map -> map.keySet()
                .stream()
                .map(CacheKey::getKey)
                .collect(Collectors.toSet()));
    }

    @Override
    public Collection<V> values() {
        return map(Map::values);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map(map -> map.entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey().getKey(), e.getValue()))
                .collect(Collectors.toSet()));
    }
}
