
package com.mycompany.lrucache;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * head - least recently / tail - most recently
 * Realization of LRU, LFU and MRU caches
 */
public class MultiPolicyCache implements Cache<Integer, Integer> {

    private final Node head = new Node(0, 0);
    private final Node tail = new Node(0, 0);
    
    Map<Integer, Node> nodeMap;
    private final TreeMap<Integer, Map<Integer, Node>> freqMap;
    
    private final CacheReplacementPolicy replacementPolicy;
        
    private final int capacity;
    private int missCount = 0;
    private int hitCount = 0;

    public MultiPolicyCache(int capacity, CacheReplacementPolicy replacementPolicy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be greater than 0.");
        }
        this.capacity = capacity;
        this.replacementPolicy = replacementPolicy;
        
        this.nodeMap = new HashMap<>(capacity);
        this.freqMap = new TreeMap<>();
        
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public Integer get(Integer key) {
        if (nodeMap.containsKey(key)) {
            hitCount++;
            Node node = nodeMap.get(key);
            if (replacementPolicy == CacheReplacementPolicy.LFU) {
                updateFrequency(node);
            }
            removeNode(node);
            addNode(node);
            return node.value;
        }
        missCount++;
        return null;
    }

    @Override
    public void put(Integer key, Integer value) {
        
        if (nodeMap.containsKey(key)){
            Node node = nodeMap.get(key);

            node.value = value;

            if (replacementPolicy == CacheReplacementPolicy.LFU) {
                updateFrequency(node);
            }
            
            removeNode(node);
            addNode(node);
            
            return;
        } else if (nodeMap.size() == capacity) {
                if (replacementPolicy == CacheReplacementPolicy.LFU) {
                    Integer leastFreq = freqMap.firstKey();  // Least Frequently Used
                    
                    Map<Integer, Node> nodesLeastFreq = freqMap.get(leastFreq);
                    Integer KeyToDel = nodesLeastFreq.keySet().iterator().next();
                    
                    Node nodeToRemove = nodesLeastFreq.get(KeyToDel);

                    nodesLeastFreq.remove(KeyToDel);
                    
                    if (nodesLeastFreq.isEmpty()) {
                        freqMap.remove(leastFreq);
                    }
                    
                    removeNode(nodeToRemove);
                } else if (replacementPolicy == CacheReplacementPolicy.LRU) {
                    removeNode(head.next); // Least Recently Used
                } else if (replacementPolicy == CacheReplacementPolicy.MRU) {
                    removeNode(tail.prev); // Most Recently Used
                }
            }
        
            if (replacementPolicy == CacheReplacementPolicy.LFU) {
                Node newNode = new Node(key, value);
                freqMap.computeIfAbsent(1, k -> new HashMap<>()).put(key, newNode);
                addNode(newNode);
            } else {
                addNode(new Node(key, value));
            }
       
    }

    @Override
    public int size() {
        return nodeMap.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void clear() {
        Node current = head.next;
        while (current != tail) {
            Node next = current.next;
            current.prev = null;
            current.next = null;
            current = next;
        }
        
        head.next = tail;
        tail.prev = head;
        nodeMap.clear();
        freqMap.clear();
    }

    public int getMissCount() {
        return missCount;
    }

    public int getHitCount() {
        return hitCount;
    }

    private void updateFrequency(Node node) {
        int oldFreq = node.frequency;
        int newFreq = oldFreq + 1;

        Map<Integer, Node> nodesAtOldFreq = freqMap.get(oldFreq);
        if (nodesAtOldFreq != null) {
            nodesAtOldFreq.remove(node.key);
            if (nodesAtOldFreq.isEmpty()) {
                freqMap.remove(oldFreq);
            }
        }

        node.frequency = newFreq;

        freqMap.computeIfAbsent(newFreq, k -> new HashMap<>()).put(node.key, node);
    }

    private void addNode(Node node) {
        nodeMap.put(node.key, node);
        Node tailPrev = tail.prev;
        tailPrev.next = node;
        node.prev = tailPrev;
        tail.prev = node;
        node.next = tail;
    }

    private void removeNode(Node node) {
        nodeMap.remove(node.key);
        Node nextNode = node.next;
        Node prevNode = node.prev;
        
        nextNode.prev = prevNode;
        prevNode.next = nextNode;
    }

    class Node {
        int key;
        int value;
        int frequency;
        Node prev;
        Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            frequency = 1;
        }
    }
}