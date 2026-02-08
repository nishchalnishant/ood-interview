# LRU Cache - LLD Interview Guide

## Problem Statement

Design a Least Recently Used (LRU) Cache with O(1) time complexity for both `get()` and `put()` operations. The cache should have a fixed capacity and evict the least recently used item when capacity is reached.

## Key Requirements

- `get(key)`: Return value if exists, -1 if not. Updates usage (moves to front).
- `put(key, value)`: Insert/update key-value pair. Evict LRU item if at capacity.
- Both operations must be **O(1)** time complexity.
- Fixed capacity defined at construction.

## Solution Approach

**Data Structures:**
- **HashMap<K, Node>**: For O(1) key lookup
- **Doubly Linked List**: For O(1) insert/delete and maintaining LRU order

**Why Doubly Linked List?**
- Head → Most recently used
- Tail → Least recently used
- O(1) to move node to head (mark as recently used)
- O(1) to remove tail (evict LRU)

## Implementation

```java
class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Node<K, V> head; // Dummy head (most recent)
    private final Node<K, V> tail; // Dummy tail (least recent)
    
    class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }
    
    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        Node<K, V> node = cache.get(key);
        moveToHead(node); // Mark as recently used
        return node.value;
    }
    
    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            // Update existing
            Node<K, V> node = cache.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            // Insert new
            Node<K, V> newNode = new Node<>(key, value);
            cache.put(key, newNode);
            addToHead(newNode);
            
            if (cache.size() > capacity) {
                // Evict LRU (tail)
                Node<K, V> lru = removeTail();
                cache.remove(lru.key);
            }
        }
    }
    
    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }
    
    private void addToHead(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private Node<K, V> removeTail() {
        Node<K, V> lru = tail.prev;
        removeNode(lru);
        return lru;
    }
}
```

## Common Interview Questions

**Q1: Why not just use LinkedHashMap?**
- In interviews, they want to see you can implement from scratch.
- `LinkedHashMap` with `accessOrder=true` provides LRU,  but doesn't demonstrate understanding.

**Q2: How would you make this thread-safe?**
```java
public synchronized V get(K key) { ... }
public synchronized void put(K key, V value) { ... }
```
Or use `ConcurrentHashMap` with `ReentrantLock` per operation.

**Q3: How would you add TTL (Time-To-Live) expiration?**
- Add `expirationTime` field to `Node`
- Background thread to check and evict expired entries
- Or check expiration in `get()` before returning

**Q4: What if we need LFU (Least Frequently Used) instead?**
- Add `frequency` counter to each node
- Maintain frequency-based doubly linked lists
- More complex but same O(1) guarantee

## Time/Space Complexity

- `get()`: O(1)
- `put()`: O(1)
- Space: O(capacity)

## Common Pitfalls

1. Using ArrayList instead of linked list (O(n) delete)
2. Not using dummy head/tail (null pointer issues)
3. Forgetting to update HashMap when removing nodes
4. Not moving node to head on `get()` (defeats LRU purpose)

## Follow-up: Distributed LRU Cache

For distributed systems (Redis-like):
- **Hash partitioning**: Route keys to specific cache servers
- **Consistent hashing**: Minimize rebalancing when servers added/removed
- **Replication**: Master-slave for high availability
- **Eviction policy**: Apply LRU per partition

---

**Key Interview Points:**
- Clearly explain why HashMap + Doubly Linked List
- Draw the data structure
- Explain eviction process step-by-step
- Discuss thread safety if asked
