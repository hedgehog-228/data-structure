package com.mycompany.lrucache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

/**
 * JUnit tests for LRU and MRU cache policies.
 */
public class LruMruCacheTest {

    /** Test LRU policy */
    @Test
    public void testLruPolicy() {
        LruMruCache cache = new LruMruCache(3, CacheReplacementPolicy.LRU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        // Access elements
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));

        // Add new element, evicting least recently used (key 1)
        cache.put(4, 400);
        assertNull(cache.get(1)); // 1 should be evicted
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));
        assertEquals(400, cache.get(4));
    }

    /** Test MRU policy */
    @Test
    public void testMruPolicy() {
        LruMruCache cache = new LruMruCache(3, CacheReplacementPolicy.MRU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        // Access elements
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));

        // Add new element, evicting most recently used (key 3)
        cache.put(4, 400);
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertNull(cache.get(3)); // 3 should be evicted
        assertEquals(400, cache.get(4));
    }

    /** Test hit and miss counters with LRU */
    @Test
    public void testHitAndMissCountersLru() {
        LruMruCache cache = new LruMruCache(2, CacheReplacementPolicy.LRU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);

        // Access elements (hits)
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(2, cache.getHitCount());
        assertEquals(0, cache.getMissCount());

        // Access a non-existing element (miss)
        assertNull(cache.get(3));
        assertEquals(2, cache.getHitCount());
        assertEquals(1, cache.getMissCount());
    }

    /** Test hit and miss counters with MRU */
    @Test
    public void testHitAndMissCountersMru() {
        LruMruCache cache = new LruMruCache(2, CacheReplacementPolicy.MRU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);

        // Access elements (hits)
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(2, cache.getHitCount());
        assertEquals(0, cache.getMissCount());

        // Access a non-existing element (miss)
        assertNull(cache.get(3));
        assertEquals(2, cache.getHitCount());
        assertEquals(1, cache.getMissCount());
    }


    /** Test LRU policy with edge cases */
    @Test
    public void testEdgeCasesLru() {
        // Edge case: Single element cache
        LruMruCache cache = new LruMruCache(1, CacheReplacementPolicy.LRU);

        cache.put(1, 100);
        assertEquals(100, cache.get(1)); // Hit
        cache.put(2, 200);
        assertNull(cache.get(1)); // Evicted
        assertEquals(200, cache.get(2)); // Hit

        // Edge case: Empty cache (no capacity)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new LruMruCache(0, CacheReplacementPolicy.LRU);
        });
        assertEquals("Cache capacity must be greater than 0.", thrown.getMessage());
    }

    /** Test MRU policy with edge cases */
    @Test
    public void testEdgeCasesMru() {
        // Edge case: Single element cache
        LruMruCache cache = new LruMruCache(1, CacheReplacementPolicy.MRU);

        cache.put(1, 100);
        assertEquals(100, cache.get(1)); // Hit
        cache.put(2, 200);
        assertNull(cache.get(1)); // Evicted
        assertEquals(200, cache.get(2)); // Hit

        // Edge case: Empty cache (no capacity)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new LruMruCache(0, CacheReplacementPolicy.MRU);
        });
        assertEquals("Cache capacity must be greater than 0.", thrown.getMessage());
    }

    /** Stress test for both LRU and MRU policies */
    @Test
    public void testStressWithRandomOperations() {
        int capacity = 100;
        int operations = 1_000_000;
        Random random = new Random();

        for (CacheReplacementPolicy policy : CacheReplacementPolicy.values()) {
            LruMruCache cache = new LruMruCache(capacity, policy);

            for (int i = 0; i < operations; i++) {
                int key = random.nextInt(1_000); // Random key
                int value = random.nextInt(10_000); // Random value

                if (random.nextBoolean()) {
                    cache.put(key, value); // Randomly add to cache
                } else {
                    cache.get(key); // Randomly access cache
                }
            }

            // Validate that cache size does not exceed capacity
            assertTrue(cache.size() <= capacity, "Cache size exceeds capacity for " + policy.getDescription());
        }
    }

    /** Stress test with hot keys (80/20 rule) */
    @Test
    public void testStressWithHotKeys() {
        int capacity = 100;
        int operations = 100_000;
        Random random = new Random();

        for (CacheReplacementPolicy policy : CacheReplacementPolicy.values()) {
            LruMruCache cache = new LruMruCache(capacity, policy);

            // Create hot keys (20% of capacity)
            int[] hotKeys = new int[capacity / 5];
            for (int i = 0; i < hotKeys.length; i++) {
                hotKeys[i] = random.nextInt(1_000);
                cache.put(hotKeys[i], random.nextInt(10_000));
            }

            for (int i = 0; i < operations; i++) {
                boolean isHot = random.nextDouble() < 0.8; // 80% chance to use hot keys
                int key = isHot ? hotKeys[random.nextInt(hotKeys.length)] : random.nextInt(1_000);

                if (random.nextBoolean()) {
                    cache.put(key, random.nextInt(10_000)); // Randomly add to cache
                } else {
                    cache.get(key); // Randomly access cache
                }
            }

            // Validate that cache size does not exceed capacity
            assertTrue(cache.size() <= capacity, "Cache size exceeds capacity for " + policy.getDescription());
        }
    }
}
