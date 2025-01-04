
package com.mycompany.lrucache;


/**
* A cache interface
*
* @param <K> the key type
* @param <V> the value type
*/
public interface Cache<K, V> {
/**
* Get the value for a key. Returns null if the key is not
* in the cache.
*
* @param key the key
*/
V get(K key);

/**
* Put a new key value pair in the cache
*
* @param key the key
* @param value the value
*/
void put(K key, V value);

/**
* Returns the current number of elements in the cache.
*
* @return the number of elements currently stored in the cache
*/
int size();

/**
* Returns the maximum number of elements the cache can hold.
*
* @return the maximum capacity of the cache
*/
int capacity();

/**
* Clears all elements from the cache.
*/
void clear();
}