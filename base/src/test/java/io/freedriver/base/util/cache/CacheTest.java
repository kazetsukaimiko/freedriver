package io.freedriver.base.util.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheTest {

    @Test
    public void testBasicMapFunctionality() {
        Cache<UUID, UUID> cache = new Cache<>();
        UUID key = UUID.randomUUID();
        UUID value = UUID.randomUUID();

        UUID putValue = cache.put(key, value);
        assertEquals(1, cache.size());
        assertNull(putValue);
        assertKeyValue(key, value, cache);
        assertEquals(0, cache.size());

        Map<UUID, UUID> uuids = IntStream.range(0, 1000)
            .boxed()
            .collect(Collectors.toMap(
                i -> UUID.randomUUID(),
                i -> UUID.randomUUID(),
                (a, b) -> b
            ));

        cache.putAll(uuids);
        assertEquals(uuids.size(), cache.size());

        uuids.forEach((k, v) -> assertKeyValue(k, v, cache));

        assertEquals(0, cache.size());
    }


    @Test
    public void testSizeLimit() {
        CacheSettings settings = new CacheSettings(10);
        Cache<UUID, UUID> cache = new Cache<>(settings);

        List<UUID> allKeys = new ArrayList<>();
        IntStream.range(0, 1000)
            .forEach(i -> {
                UUID key = UUID.randomUUID();
                allKeys.add(key);
                cache.put(key, UUID.randomUUID());
                assertTrue(cache.size() <= settings.getMaxSize());
                if (cache.size() == settings.getMaxSize()) {
                    allKeys.subList(0, (int) (allKeys.size() - settings.getMaxSize()))
                            .forEach(deletedKey -> assertFalse(cache.containsKey(deletedKey)));
                }
            });
    }


    @Test
    public void testExpiry() {
        Duration wait = Duration.ofMillis(2);
        CacheSettings settings = new CacheSettings(Duration.ofMillis(1));
        Cache<UUID, UUID> cache = new Cache<>(settings);

        IntStream.range(0, 100)
                .forEach(i -> {
                    UUID key = UUID.randomUUID();
                    cache.put(key, UUID.randomUUID());
                    sleep(wait);
                    assertFalse(cache.containsKey(key));
                });
    }



    public static  <K, V> void assertKeyValue(K key, V value, Cache<K, V> cache) {
        assertTrue(cache.containsKey(key));
        assertFalse(cache.containsKey(value));
        assertTrue(cache.containsValue(value));
        assertFalse(cache.containsValue(key));

        V getValue = cache.get(key);
        assertEquals(value, getValue);

        V removeValue = cache.remove(key);
        assertEquals(value, removeValue);

        assertFalse(cache.containsKey(key));
        assertFalse(cache.containsValue(value));
    }

    public void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
