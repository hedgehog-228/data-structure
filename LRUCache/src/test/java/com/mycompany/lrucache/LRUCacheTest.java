/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.lrucache;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;


/**
 * JUnit tests for LPUCache.
 */

public class LRUCacheTest {
    
    /** Test basic functionality of the cache */
    @Test
    public void testBasicFunctionality() {
        LRUCache cache = new LRUCache(3);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        // Validate retrieval
        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));

        // Add another element and check eviction
        cache.put(4, 400);
        assertNull(cache.get(1)); // 1 should be evicted
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));
        assertEquals(400, cache.get(4));

        // Update existing key
        cache.put(2, 250);
        assertEquals(250, cache.get(2));
    }

    /** Test edge cases */
    @Test
    public void testEdgeCases() {
        // Test invalid cache capacity
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new LRUCache(0);
        });
        assertEquals("Cache capacity must be greater than 0.", thrown.getMessage());

        // Test single element cache
        LRUCache singleCache = new LRUCache(1);
        singleCache.put(1, 100);
        assertEquals(100, singleCache.get(1));
        singleCache.put(2, 200);
        assertNull(singleCache.get(1)); // 1 should be evicted
        assertEquals(200, singleCache.get(2));

        // Test with Integer.MIN_VALUE and Integer.MAX_VALUE
        LRUCache edgeCache = new LRUCache(2);
        edgeCache.put(Integer.MIN_VALUE, -1);
        edgeCache.put(Integer.MAX_VALUE, 1);
        assertEquals(-1, edgeCache.get(Integer.MIN_VALUE));
        assertEquals(1, edgeCache.get(Integer.MAX_VALUE));
    }

    /** Stress test with a large number of operations */
    @Test
    public void testStress() {
        int capacity = 1000;
        int operations = 1_000_000;
        LRUCache cache = new LRUCache(capacity);
        Random random = new Random();

        for (int i = 0; i < operations; i++) {
            int key = random.nextInt(10_000);
            int value = random.nextInt(10_000);

            if (random.nextBoolean()) {
                cache.put(key, value);
            } else {
                cache.get(key);
            }
        }

        // Validate size does not exceed capacity
        assertTrue(cache.size() <= capacity);
    }

    /** Test eviction policy correctness */
    @Test
    public void testEvictionPolicy() {
        LRUCache cache = new LRUCache(3);

        // Add elements
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        // Access 1 to make it most recently used
        assertEquals(100, cache.get(1));

        // Add new element, evicting least recently used (2)
        cache.put(4, 400);
        assertNull(cache.get(2)); // 2 should be evicted

        // Check remaining elements
        assertEquals(100, cache.get(1));
        assertEquals(300, cache.get(3));
        assertEquals(400, cache.get(4));
    }

    /** Test clear functionality */
    @Test
    public void testClear() {
        LRUCache cache = new LRUCache(5);
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);

        cache.clear();

        // Validate cache is empty
        assertEquals(0, cache.size());
        assertNull(cache.get(1));
        assertNull(cache.get(2));
        assertNull(cache.get(3));
    }
    
}
