
package com.mycompany.lrucache;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the LPUCache demo!");
        System.out.print("Enter the capacity of the cache: ");
        int capacity = scanner.nextInt();

        LRUCache cache = new LRUCache(capacity);

        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Put a key-value pair");
            System.out.println("2. Get a value by key");
            System.out.println("3. Display cache size");
            System.out.println("4. Display cache capacity");
            System.out.println("5. Clear the cache");
            System.out.println("6. Exit");
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
                    System.out.print("Enter key to retrieve: ");
            int key = scanner.nextInt();
            Integer retrievedValue = cache.get(key);
            if (retrievedValue != null) {
                System.out.println("Value: " + retrievedValue);
            } else {
                System.out.println("Key not found in cache.");
            }
        }
                case 3 -> System.out.println("Current cache size: " + cache.size());
                case 4 -> System.out.println("Cache capacity: " + cache.capacity());
                case 5 -> {
                    cache.clear();
                    System.out.println("Cache cleared.");
        }
                case 6 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
        }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
