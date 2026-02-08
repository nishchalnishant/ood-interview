# Rate Limiter - LLD Interview Guide

## Problem Statement

Design a rate limiter that restricts the number of requests a user can make within a given time window. Support multiple rate limiting algorithms and handle distributed scenarios.

## Key Algorithms

### 1. Token Bucket
- Bucket has capacity of N tokens
- Tokens added at fixed rate (R tokens/second)
- Request consumes 1 token
- If tokens available → allow request, else deny

```java
class TokenBucket {
    private final long capacity;
    private final long refillRate; // tokens per second
    private long tokens;
    private long lastRefillTime;
    
    public TokenBucket(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }
    
    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }
    
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        long tokensToAdd = (elapsed / 1000) * refillRate;
        
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastRefillTime = now;
    }
}
```

### 2. Fixed Window Counter
- Fixed time windows (e.g., per minute)
- Count requests in current window
- Reset counter when window expires

```java
class FixedWindowCounter {
    private final int maxRequests;
    private final long windowSizeMs;
    private int counter = 0;
    private long windowStart;
    
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        if (now - windowStart >= windowSizeMs) {
            // New window
            counter = 0;
            windowStart = now;
        }
        
        if (counter < maxRequests) {
            counter++;
            return true;
        }
        return false;
    }
}
```

**Problem**: Burst at window boundaries (e.g., 100 req at 00:59, 100 req at 01:00 → 200 in 1 second)

### 3. Sliding Window Log
- Store timestamp of each request
- Remove old timestamps outside window
- Count remaining timestamps

```java
class SlidingWindowLog {
    private final int maxRequests;
    private final long windowSizeMs;
    private final Queue<Long> timestamps = new LinkedList<>();
    
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        
        // Remove old timestamps
        while (!timestamps.isEmpty() && now - timestamps.peek() >= windowSizeMs) {
            timestamps.poll();
        }
        
        if (timestamps.size() < maxRequests) {
            timestamps.offer(now);
            return true;
        }
        return false;
    }
}
```

**Memory Issue**: Stores all request timestamps

### 4. Sliding Window Counter (Hybrid)
- Combines fixed window efficiency with sliding accuracy
- Less memory than log, more accurate than fixed window

## Distributed Rate Limiting

For multiple servers use **Redis**:

```java
class RedisRateLimiter {
    private final Jedis redis;
    private final String keyPrefix;
    
    public boolean allowRequest(String userId) {
        String key = keyPrefix + userId;
        long now = System.currentTimeMillis();
        
        // Sliding window with Redis sorted set
        redis.zremrangeByScore(key, 0, now - windowSizeMs);
        long count = redis.zcard(key);
        
        if (count < maxRequests) {
            redis.zadd(key, now, UUID.randomUUID().toString());
            redis.expire(key, (int) (windowSizeMs / 1000));
            return true;
        }
        return false;
    }
}
```

## Common Interview Questions

**Q1: Token Bucket vs Leaky Bucket?**
- **Token Bucket**: Allows bursts up to capacity
- **Leaky Bucket**: Smooth constant rate, no bursts

**Q2: How to handle different rate limits per user tier (free vs premium)?**
```java
enum UserTier {
    FREE(100), PREMIUM(1000), ENTERPRISE(10000);
    private final int rateLimit;
}

RateLimiter getRateLimiter(User user) {
    return new TokenBucket(user.getTier().getRateLimit(), ...);
}
```

**Q3: How to rate limit specific APIs differently?**
- Separate rate limiters per API endpoint
- Store in Map<String, RateLimiter> keyed by endpoint

## Trade-offs

| Algorithm | Pros | Cons |
|-----------|------|------|
| Token Bucket | Handles bursts | Complex implementation |
| Fixed Window | Simple, memory efficient | Burst at boundaries |
| Sliding Log | Accurate | High memory usage |
| Sliding Counter | Good balance | More complex |

## Key Topics
- Token bucket vs leaky bucket
- Distributed rate limiting with Redis
- Memory vs accuracy trade-offs
- Per-user vs per-API limits
