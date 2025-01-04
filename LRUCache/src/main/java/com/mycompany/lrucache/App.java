
package com.mycompany.lrucache;

import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        LRUCache cache = null;
        
        System.out.println("Welcome to the LPUCache demo!");
        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Your own cache");
            System.out.println("2. Random cache");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter the capacity of the cache: ");
                    int capacity = scanner.nextInt();
                    cache = new LRUCache(capacity);
                    System.out.println("Cache created with capacity " + capacity + ".");
                }
                
                case 2 -> {
                    int capacity = 100;
                    cache = new LRUCache(capacity);

                    for (int i = 0; i < capacity; i++) {
                        int randomKey = random.nextInt(1000); // Random key [0, 999]
                        int randomValue = random.nextInt(10000); // Random value [0, 9999]
                        cache.put(randomKey, randomValue);
                    }

                    System.out.println("Cache created with random data (100 keys).");
                }
                
                case 3 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                
                default -> System.out.println("Invalid choice. Please try again.");
            }

            if (cache != null) break; // Exit loop after creating a cache
        }
        
        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Put a key-value pair");
            System.out.println("2. Random");
            System.out.println("3. Get a value by key");
            System.out.println("4. Display cache size");
            System.out.println("5. Display cache capacity");
            System.out.println("6. Clear the cache");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter key: ");
                    int key = scanner.nextInt();
                    System.out.print("Enter value: ");
                    int value = scanner.nextInt();
                    cache.put(key, value);
                    System.out.println("Key-value pair added.");
                }
                
                case 2 -> {                  
                    Integer[] keys = new Integer[cache.nodeMap.size()];
                    cache.nodeMap.keySet().toArray(keys);
                    
                    if (keys.length == 0) {
                        System.out.println("Cache is empty. No keys to select.");
                        break;
                    }
                    // Calculation 20% "hot" keys
                    int hotKeysCount = (int) Math.ceil(keys.length * 0.2); // 20% from length = hotKeysCount
                    Integer[] hotKeys = new Integer[hotKeysCount];
                    System.arraycopy(keys, 0, hotKeys, 0, hotKeysCount); // copy key[for 0 < hotKeysCount] from keys[] to hotKeys[] 

                    // random generated random access
                    int accessCount = random.nextInt(100000);
                    System.out.println("Total operations: " + accessCount);

                    for (int i = 0; i < accessCount; i++) {
                        boolean isHot = random.nextDouble() < 0.8; // 80% hot keys
                        int randomKey;

                        if (isHot) {
                            randomKey = hotKeys[random.nextInt(hotKeys.length)];
                        } else {
                            randomKey = random.nextInt(2000); 
                        }

                        cache.get(randomKey);
                    }
                    
                    System.out.println("Cache hits: " + cache.getHitCount());
                    System.out.println("Cache misses: " + cache.getMissCount());
                    System.out.printf("Hit Rate: %.2f%%\n", (cache.getHitCount() / (double) accessCount) * 100);
                    System.out.printf("Miss Rate: %.2f%%\n", (cache.getMissCount() / (double) accessCount) * 100);
                    
                }
                
                case 3 -> {
                    System.out.print("Enter key to retrieve: ");
                    int key = scanner.nextInt();
                    Integer retrievedValue = cache.get(key);
                    if (retrievedValue != null) {
                        System.out.println("Value: " + retrievedValue);
                    } else {
                        System.out.println("Key not found in cache.");
                    }
                }
                
                case 4 -> System.out.println("Current cache size: " + cache.size());
                
                case 5 -> System.out.println("Cache capacity: " + cache.capacity());
                
                case 6 -> {
                    cache.clear();
                    System.out.println("Cache cleared.");
                }
                
                case 7 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
        }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
