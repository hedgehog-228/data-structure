
package com.mycompany.lpucache;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * head - least recent / tail - most recent
 */
public class LPUCache implements Cache<Integer, Integer> {
    
    private final Node head = new Node(0,0);
    private final Node tail = new Node(0,0);
    
    Map<Integer, Node> nodeMap;
    private int capacity;
    
    
    public LPUCache(int capacity){
        if (capacity <= 0) {
        throw new IllegalArgumentException("Cache capacity must be greater than 0.");
        }
        nodeMap = new HashMap<>(capacity);
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public Integer get(Integer key) {
        
        if (nodeMap.containsKey(key)) {
            Node node = nodeMap.get(key);
            removeNode(node);
            addNode(node);
            return node.value;
        }
        
        return null;
    }

    @Override
    public void put(Integer key, Integer value) {

        if (nodeMap.containsKey(key)){
            removeNode(nodeMap.get(key));
        } else {
            if (nodeMap.size() == capacity) {
                removeNode(head.next);
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
