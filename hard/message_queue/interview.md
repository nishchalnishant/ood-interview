# Distributed Message Queue (Kafka-like) - LLD Interview Guide

## Problem Statement

Design a distributed message queue system like Apache Kafka that allows producers to publish messages and consumers to subscribe to topics with high throughput, fault tolerance, and message ordering guarantees.

## Clarifying Questions

1. **Throughput**: Expected messages per second?
2. **Message Size**: Average and maximum message size?
3. **Ordering**: Strict ordering required or per-partition ordering sufficient?
4. **Delivery Guarantee**: At-most-once, at-least-once, or exactly-once?
5. **Retention**: How long to retain messages? (hours, days, forever?)
6. **Consumers**: Multiple consumer groups? Replay capability needed?
7. **Persistence**: In-memory only or disk persistence?

## Core Requirements

### Functional Requirements
- Producers can publish messages to topics
- Consumers can subscribe to topics
- Topics partitioned for parallelism
- Message ordering within partitions
- Consumer groups for load balancing
- Message retention (time-based or size-based)
- Offset management (track consumer progress)

### Non-Functional Requirements
- **High Throughput**: Millions of messages/sec
- **Low Latency**: < 10ms p99 latency
- **Scalability**: Horizontal scaling (add brokers/partitions)
- **Durability**: Persist messages to disk
- **Fault Tolerance**: No data loss on broker failure
- **Availability**: System remains operational during failures

## Key Components

| Component | Responsibility |
|-----------|---------------|
| `Producer` | Publishes messages to topics |
| `Consumer` | Consumes messages from topics |
| `Topic` | Logical category for messages |
| `Partition` | Physical subdivision of topic for parallelism |
| `Broker` | Server that stores and serves messages |
| `Message` | Data unit with key, value, timestamp |
| `ConsumerGroup` | Group of consumers for load balancing |
| `ZooKeeper/Controller` | Manages cluster metadata and leader election |
| `Offset` | Position in partition (consumer progress) |

## Architecture

```
Producers                      Message Queue Cluster                      Consumers
┌──────────┐                                                            ┌──────────┐
│Producer 1├─┐          ┌──────────────────────────────────┐          ┌─┤Consumer 1│
└──────────┘ │          │  Broker 1 (Leader for P0, P2)   │          │ └──────────┘
             │          │  ┌─────────┬─────────┬─────────┐ │          │
┌──────────┐ │Messages  │  │Topic A  │Topic A  │Topic B  │ │          │ ┌──────────┐
│Producer 2├─┼─────────▶│  │Partition│Partition│Partition│ │──Messages├─┤Consumer 2│
└──────────┘ │          │  │   0     │   1     │   0     │ │          │ └──────────┘
             │          │  └─────────┴─────────┴─────────┘ │          │    (Group 1)
┌──────────┐ │          └──────────────────────────────────┘          │
│Producer 3├─┘                                                         │ ┌──────────┐
└──────────┘            ┌──────────────────────────────────┐          └─┤Consumer 3│
                        │  Broker 2 (Leader for P1)        │            └──────────┘
                        │  ┌─────────┬─────────┐           │
                        │  │Topic A  │Topic B  │           │            ┌──────────┐
                        │  │Partition│Partition│           │          ┌─┤Consumer 4│
                        │  │   2     │   1     │           │          │ └──────────┘
                        │  └─────────┴─────────┘           │          │    (Group 2)
                        └──────────────────────────────────┘          │ ┌──────────┐
                                     ▲                                 └─┤Consumer 5│
                        ┌────────────┴─────────────┐                    └──────────┘
                        │  ZooKeeper / Controller  │
                        │  (Cluster Coordination)  │
                        └──────────────────────────┘
```

## Core Classes

```java
class Message {
    private String key;         // For partitioning
    private byte[] value;       // Actual message content
    private long timestamp;
    private Map<String, String> headers;
}

class Topic {
    private String name;
    private int numPartitions;
    private int replicationFactor;
    private RetentionPolicy retention;
    private List<Partition> partitions;
}

class Partition {
    private int partitionId;
    private String topicName;
    private Broker leader;              // Current leader
    private List<Broker> replicas;      // In-sync replicas (ISR)
    private long currentOffset;         // Next offset to write
    
    // Log segments (files on disk)
    private List<LogSegment> segments;
    
    public void append(Message message) {
        long offset = currentOffset++;
        LogSegment activeSegment = getActiveSegment();
        activeSegment.append(offset, message);
        
        // Replicate to followers
        replicateToFollowers(offset, message);
    }
    
    public List<Message> read(long startOffset, int maxMessages) {
        return segments.stream()
            .flatMap(segment -> segment.read(startOffset, maxMessages).stream())
            .collect(Collectors.toList());
    }
}

class Producer {
    private String clientId;
    private Map<String, Partition> partitionCache;
    
    public Future<RecordMetadata> send(String topic, String key, byte[] value) {
        // 1. Get partition for this key
        Partition partition = selectPartition(topic, key);
        
        // 2. Serialize message
        Message message = new Message(key, value, System.currentTimeMillis());
        
        // 3. Send to broker (async)
        return sendAsync(partition, message);
    }
    
    private Partition selectPartition(String topic, String key) {
        if (key != null) {
            // Hash-based partitioning
            int hash = Math.abs(key.hashCode());
            int partitionId = hash % getNumPartitions(topic);
            return getPartition(topic, partitionId);
        } else {
            // Round-robin
            return roundRobinPartition(topic);
        }
    }
}

class Consumer {
    private String groupId;
    private Set<String> subscribedTopics;
    private Map<Partition, Long> offsets;
    
    public List<Message> poll(long timeoutMs) {
        List<Message> messages = new ArrayList<>();
        
        for (Partition partition : assignedPartitions) {
            long offset = offsets.getOrDefault(partition, 0L);
            List<Message> batch = partition.read(offset, 100);
            messages.addAll(batch);
            
            // Update offset
            if (!batch.isEmpty()) {
                offsets.put(partition, offset + batch.size());
            }
        }
        
        return messages;
    }
    
    public void commitOffsets() {
        // Commit current offsets to broker
        for (Map.Entry<Partition, Long> entry : offsets.entrySet()) {
            offsetManager.commit(groupId, entry.getKey(), entry.getValue());
        }
    }
}

class ConsumerGroup {
    private String groupId;
    private List<Consumer> consumers;
    private Map<Consumer, Set<Partition>> assignments;
    
    // Rebalance when consumer joins/leaves
    public void rebalance() {
        List<Partition> allPartitions = getAllPartitionsFromTopics();
        int partitionsPerConsumer = allPartitions.size() / consumers.size();
        
        // Simple round-robin assignment
        assignments.clear();
        for (int i = 0; i < allPartitions.size(); i++) {
            Consumer consumer = consumers.get(i % consumers.size());
            assignments.computeIfAbsent(consumer, k -> new HashSet<>())
                .add(allPartitions.get(i));
        }
    }
}
```

## Common Interview Questions

**Q1: How do you ensure message ordering?**

- **Ordering Guarantee**: Messages are ordered **within a partition**, not across partitions
- **Strategy**: Messages with same key go to same partition
- **Implementation**: Hash(key) % numPartitions

```java
// Producer sends orders for same user to same partition
producer.send("orders", userId, orderData);
// All orders for userId=123 go to same partition → ordered
```

**Q2: How do you handle broker failures?**

**Replication:**
- Each partition has 1 leader + N replicas (followers)
- **In-Sync Replicas (ISR)**: Replicas that are up-to-date
- **Leader Election**: If leader fails, elect new leader from ISR

```java
class Partition {
    private Broker leader;
    private Set<Broker> isr; // In-Sync Replicas
    
    void handleLeaderFailure() {
        // ZooKeeper detects leader failure
        if (leader.isDown()) {
            // Elect new leader from ISR
            Broker newLeader = isr.iterator().next();
            this.leader = newLeader;
            // Notify all consumers/producers
        }
    }
}
```

**Q3: How do you achieve exactly-once semantics?**

**Idempotent Producer:**
- Assign unique ID to each message
- Broker deduplicates based on ID

**Transactional Writes:**
- Producer writes to multiple partitions atomically
- Use 2-phase commit protocol

```java
producer.beginTransaction();
try {
    producer.send("topic1", message1);
    producer.send("topic2", message2);
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

**Q4: How do you scale the system?**

**Horizontal Scaling:**
1. **Add more brokers** to cluster
2. **Increase partitions** for topic (more parallelism)
3. **Add more consumers** to consumer group (up to #partitions)

**Limitations:**
- Can't have more consumers than partitions in a group
- Rebalancing overhead when adding/removing consumers

**Q5: How do you implement message retention?**

```java
class RetentionPolicy {
    private long retentionMs;      // Time-based: 7 days
    private long retentionBytes;   // Size-based: 1 GB
    
    void cleanup(Partition partition) {
        // Delete old segments
        for (LogSegment segment : partition.getSegments()) {
            if (segment.getEndTimestamp() < now() - retentionMs ||
                partition.getTotalSize() > retentionBytes) {
                segment.delete();
            }
        }
    }
}
```

## Performance Optimizations

**1. Batch Writes:**
```java
// Producer batches messages before sending
List<Message> batch = new ArrayList<>();
while (batch.size() < BATCH_SIZE && !timeout()) {
    batch.add(queue.poll());
}
broker.send(batch);
```

**2. Zero-Copy Transfer:**
- Use `sendfile()` system call to transfer data from disk to network
- Avoid copying data through application memory

**3. Sequential Disk I/O:**
- Append-only log structure
- Sequential writes are fast (>100 MB/s even on HDD)

**4. Compression:**
```java
message.setCompression(CompressionType.SNAPPY);
// Reduce network and storage
```

## Trade-offs

| Aspect | Choice | Trade-off |
|--------|--------|-----------|
| **Ordering** | Per-partition only | Can't guarantee global order across partitions |
| **Throughput vs Latency** | Batch writes | Higher throughput but increased latency |
| **Durability** | Replicate to ISR | Slower writes but no data loss |
| **Consistency** | Leader-based | Reads/writes go through leader (potential bottleneck) |

## Testing Strategy

- **Unit Tests**: Partition assignment, offset management
- **Integration Tests**: Producer-broker-consumer flow
- **Fault Injection**: Kill broker, simulate network partition
- **Load Tests**: Millions of messages/sec throughput
- **Chaos Engineering**: Random failures in production-like environment

## Real-World Considerations

- **Monitoring**: Track lag per consumer group, broker CPU/disk
- **Alerting**: If consumer lag > threshold, broker down
- **Schema Management**: Use Avro/Protobuf with schema registry
- **Security**: TLS encryption, SASL authentication, ACLs
- **Multi-tenancy**: Quotas per client, topic isolation

---

**Key Interview Points:**
- Partitioning for parallelism and ordering
- Replication and leader election
- Offset management
- Consumer groups and rebalancing
- Exactly-once semantics (idempotence + transactions)
- Performance: batching, zero-copy, sequential I/O
