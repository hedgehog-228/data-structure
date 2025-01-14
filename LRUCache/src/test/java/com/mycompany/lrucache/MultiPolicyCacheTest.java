package com.mycompany.lrucache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

/**
 * JUnit tests for LRU, LFU and MRU caches policies.
 */
public class MultiPolicyCacheTest {

    /* Test LRU policy */
    @Test
    public void testLruPolicy() {
        MultiPolicyCache cache = new MultiPolicyCache(3, CacheReplacementPolicy.LRU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        // Access elements
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));

        cache.put(4, 400);
        assertNull(cache.get(1)); // 1 should be evicted
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));
        assertEquals(400, cache.get(4));
    }

    /* Test MRU policy */
    @Test
    public void testMruPolicy() {
        MultiPolicyCache cache = new MultiPolicyCache(3, CacheReplacementPolicy.MRU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        // Access elements
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));

        cache.put(4, 400);
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertNull(cache.get(3)); // 3 should be evicted
        assertEquals(400, cache.get(4));
    }
    
    /* Test LFU policy */
    @Test
    public void testLfuPolicy() {
        MultiPolicyCache cache = new MultiPolicyCache(3, CacheReplacementPolicy.LFU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        // Access elements to increase frequencies
        cache.get(1); // Frequency  = 2
        cache.get(2); 
        cache.get(2); // Frequency = 3

        cache.put(4, 400);
        assertNull(cache.get(3)); // 3 should be evicted
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(400, cache.get(4));
        
        cache.get(1); // Frequency  = 3
        cache.get(4);
        cache.get(4); // Frequency  = 3
        
        cache.put(5, 500);
        // (1) F = 3 (2) F = 3 (3) F = 3
        assertNull(cache.get(1));
    }

    /* Test hit and miss counters with LRU */
    @Test
    public void testHitAndMissCounters() {
        MultiPolicyCache cache = new MultiPolicyCache(2, CacheReplacementPolicy.LRU);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);

        // access elements (hits)
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(2, cache.getHitCount());
        assertEquals(0, cache.getMissCount());

        // Access a non-existing element (miss)
        assertNull(cache.get(3));
        assertEquals(2, cache.getHitCount());
        assertEquals(1, cache.getMissCount());
    }

    /* Test LRU policy with edge cases */
    @Test
    public void testEdgeCasesLru() {
        // Single element cache
        MultiPolicyCache cache = new MultiPolicyCache(1, CacheReplacementPolicy.LRU);

        cache.put(1, 100);
        assertEquals(100, cache.get(1)); // Hit
        cache.put(2, 200);
        assertNull(cache.get(1)); // Evicted
        assertEquals(200, cache.get(2)); // Hit

        // Empty cache (no capacity)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new MultiPolicyCache(0, CacheReplacementPolicy.LRU);
        });
        assertEquals("Cache capacity must be greater than 0.", thrown.getMessage());
    }

    /* Test MRU policy with edge cases */
    @Test
    public void testEdgeCasesMru() {
        // Single element cache
        MultiPolicyCache cache = new MultiPolicyCache(1, CacheReplacementPolicy.MRU);

        cache.put(1, 100);
        assertEquals(100, cache.get(1)); // Hit
        cache.put(2, 200);
        assertNull(cache.get(1)); // Evicted
        assertEquals(200, cache.get(2)); // Hit

        // Empty cache (no capacity)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new MultiPolicyCache(0, CacheReplacementPolicy.MRU);
        });
        assertEquals("Cache capacity must be greater than 0.", thrown.getMessage());
    }
    
    
    /* Test LFU policy with edge cases */
    @Test
    public void testEdgeCasesLfu() {
        // Single element cache
        MultiPolicyCache cache = new MultiPolicyCache(1, CacheReplacementPolicy.LFU);

        cache.put(1, 100);
        assertEquals(100, cache.get(1)); // Hit
        cache.put(2, 200);
        assertNull(cache.get(1)); // evicted due to capacity
        assertEquals(200, cache.get(2)); // Hit

        // empty cache (no capacity)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new MultiPolicyCache(0, CacheReplacementPolicy.LFU);
        });
        assertEquals("Cache capacity must be greater than 0.", thrown.getMessage());
    }


    /* Stress test for LRU, LFU and MRU policies */
    @Test
    public void testStressWithRandomOperations() {
        int capacity = 100;
        int operations = 1_000_000;
        Random random = new Random();

        for (CacheReplacementPolicy policy : CacheReplacementPolicy.values()) {
            MultiPolicyCache cache = new MultiPolicyCache(capacity, policy);

            for (int i = 0; i < operations; i++) {
                int key = random.nextInt(1_000); // Random key
                int value = random.nextInt(10_000); // Random value

                if (random.nextBoolean()) {
                    cache.put(key, value); // Randomly add to cache
                } else {
                    cache.get(key); // Randomly access cache
                }
            }

            // cache size does not exceed capacity
            assertTrue(cache.size() <= capacity, "Cache size exceeds capacity for " + policy.getDescription());
        }
    }

    /* Stress test with hot keys (80/20 rule) */
    @Test
    public void testStressWithHotKeysUpdated() {
        int capacity = 100;
        int operations = 100_000;
        Random random = new Random();

        for (CacheReplacementPolicy policy : CacheReplacementPolicy.values()) {
            MultiPolicyCache cache = new MultiPolicyCache(capacity, policy);

            // start input of cache
            for (int i = 0; i < capacity; i++) {
                cache.put(i, random.nextInt(10_000));
            }

            // creating hot keys with random + from cache keys 
            Integer[] keys = cache.nodeMap.keySet().toArray(new Integer[0]);
            int hotKeysCount = (int) Math.ceil(keys.length * 0.2); 
            Integer[] hotKeys = new Integer[hotKeysCount];
            for (int i = 0; i < hotKeysCount; i++) {
                if (i % 2 == 0 && i < keys.length) {
                    hotKeys[i] = keys[i];
                } else {
                    hotKeys[i] = random.nextInt(2000);
                }
            }

            for (int i = 0; i < operations; i++) {
                boolean isHot = random.nextDouble() < 0.8; // 80% of hot keys
                int key = isHot
                        ? hotKeys[random.nextInt(hotKeys.length)]
                        : random.nextInt(2000);

                if (random.nextBoolean()) {
                    cache.put(key, random.nextInt(10_000)); // random adding
                } else {
                    cache.get(key); // random access
                }
            }

            // cache size does not exceed capacity
            assertTrue(cache.size() <= capacity, "Cache size exceeds capacity for " + policy.getDescription());
        }
    }

}
