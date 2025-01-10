
package com.mycompany.lrucache;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * head - least recent / tail - most recent
 * Realization of LRU and MRU cache
 */
public class LruMruCache implements Cache<Integer, Integer> {
    
    private final Node head = new Node(0,0);
    private final Node tail = new Node(0,0);
    Map<Integer, Node> nodeMap;
    private int capacity;
    private final CacheReplacementPolicy replacementPolicy;
    
    private int missCount = 0;
    private int hitCount = 0;

    public LruMruCache(int capacity, CacheReplacementPolicy replacementPolicy){
        if (capacity <= 0) {
        throw new IllegalArgumentException("Cache capacity must be greater than 0.");
        }
        nodeMap = new HashMap<>(capacity);
        this.capacity = capacity;
        this.replacementPolicy = replacementPolicy;
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public Integer get(Integer key) {
        
        if (nodeMap.containsKey(key)) {
            hitCount++;
            Node node = nodeMap.get(key);
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
            removeNode(nodeMap.get(key));
        } else if (nodeMap.size() == capacity) {
                if (replacementPolicy == CacheReplacementPolicy.LRU) {
                    removeNode(head.next); // remove least recent used
                } else  if (replacementPolicy == CacheReplacementPolicy.MRU) {
                    removeNode(tail.prev); // remove most recent used 
                } 
            }
       
        addNode(new Node(key,value));
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
    }
    
    public int getMissCount() {
        return missCount;
    }

    public int getHitCount() {
        return hitCount;
    }
    
    
    public void addNode(Node node) {
        nodeMap.put(node.key, node);
        Node tailPrev = tail.prev;
        node.prev = tailPrev;
        tailPrev.next =  node;
        tail.prev = node;
        node.next = tail; 
    }
    
    public void removeNode(Node node) {
       nodeMap.remove(node.key);
       Node nextNode = node.next;
       Node prevNode = node.prev;
       
       nextNode.prev = prevNode;
       prevNode.next = nextNode;
    }

    class Node{
        int key; 
        int value; 
        Node prev; 
        Node next; 

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
