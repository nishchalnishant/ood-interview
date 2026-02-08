# Search Autocomplete System - LLD Interview Guide

## Problem Statement

Design a search autocomplete/typeahead system that suggests search queries as users type, providing relevant suggestions quickly with ranking based on popularity and personalization.

## Core Requirements

### Functional
- Return top K suggestions as user types
- Rank by popularity (search frequency)
- Support prefix matching
- Handle typos and fuzzy matching
- Personalized suggestions (user history)
- Real-time updates (trending searches)

### Non-Functional
- **Low Latency**: < 100ms response time
- **Scalability**: Handle millions of queries/sec
- **Availability**: 99.9% uptime
- **Storage**: Billions of search queries

## Core Data Structure: Trie

```java
class TrieNode {
    private Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private int frequency;              // How often this query was searched
    private List<String> topSuggestions; // Cache top K suggestions at each node
    
    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        frequency = 0;
        topSuggestions = new ArrayList<>();
    }
}

class Trie {
    private TrieNode root;
    private static final int MAX_SUGGESTIONS = 10;
    
    public Trie() {
        root = new TrieNode();
    }
    
    // Insert query with frequency
    public void insert(String query, int frequency) {
        TrieNode current = root;
        
        for (char ch : query.toLowerCase().toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        
        current.isEndOfWord = true;
        current.frequency += frequency;
        
        // Update top suggestions along the path
        updateTopSuggestions(query, frequency);
    }
    
    // Get autocomplete suggestions for prefix
    public List<String> getSuggestions(String prefix) {
        TrieNode current = root;
        
        // Navigate to prefix node
        for (char ch : prefix.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return Collections.emptyList(); // No matches
            }
            current = current.children.get(ch);
        }
        
        // Return cached top suggestions at this node
        return current.topSuggestions;
    }
    
    private void updateTopSuggestions(String query, int frequency) {
        TrieNode current = root;
        
        for (char ch : query.toLowerCase().toCharArray()) {
            current = current.children.get(ch);
            
            // Update this node's top suggestions
            List<QueryWithFreq> suggestions = new ArrayList<>(current.topSuggestions.stream()
                .map(s -> new QueryWithFreq(s, getFrequency(s)))
                .collect(Collectors.toList()));
            
            suggestions.add(new QueryWithFreq(query, frequency));
            
            // Sort by frequency DESC and take top K
            current.topSuggestions = suggestions.stream()
                .sorted(Comparator.comparing(QueryWithFreq::getFrequency).reversed())
                .limit(MAX_SUGGESTIONS)
                .map(QueryWithFreq::getQuery)
                .collect(Collectors.toList());
        }
    }
}

class AutocompleteService {
    private final Trie trie;
    private final Cache<String, List<String>> cache; // Redis
    
    public List<String> getSuggestions(String prefix, String userId) {
        // Check cache first
        String cacheKey = "suggest:" + prefix.toLowerCase();
        List<String> cached = cache.get(cacheKey);
        if (cached != null) return cached;
        
        // Get from Trie
        List<String> suggestions = trie.getSuggestions(prefix);
        
        // Personalize based on user history
        if (userId != null) {
            suggestions = personalize(suggestions, userId);
        }
        
        // Cache result (short TTL for trending queries)
        cache.set(cacheKey, suggestions, Duration.ofMinutes(5));
        
        return suggestions;
    }
    
    public void recordSearch(String query, String userId) {
        // Increment query frequency
        trie.insert(query, 1);
        
        // Store in user history
        if (userId != null) {
            userHistoryService.addSearch(userId, query);
        }
        
        // Invalidate cache for this prefix
        for (int i = 1; i <= query.length(); i++) {
            cache.delete("suggest:" + query.substring(0, i));
        }
    }
    
    private List<String> personalize(List<String> suggestions, String userId) {
        Set<String> userHistory = userHistoryService.getRecentSearches(userId);
        
        // Boost queries user has searched before
        return suggestions.stream()
            .sorted((a, b) -> {
                boolean aInHistory = userHistory.contains(a);
                boolean bInHistory = userHistory.contains(b);
                if (aInHistory && !bInHistory) return -1;
                if (!aInHistory && bInHistory) return 1;
                return 0;
            })
            .collect(Collectors.toList());
    }
}
```

## Common Interview Questions

**Q1: How do you handle typos and fuzzy matching?**

**Option 1: Edit Distance (Levenshtein)**
```java
public List<String> getFuzzySuggestions(String prefix) {
    List<String> allQueries = getAllQueries(); // From database
    
    return allQueries.stream()
        .filter(q -> editDistance(q, prefix) <= 2) // Max 2 edits
        .sorted(Comparator.comparing(q -> editDistance(q, prefix)))
        .limit(10)
        .collect(Collectors.toList());
}
```

**Option 2: Phonetic (Soundex/Metaphone)**
- Convert queries to phonetic codes
- "knight" and "night" → same code

**Q2: How do you handle trending/real-time queries?**

```java
class TrendingService {
    private final SlidingWindowCounter counter;
    
    public void recordSearch(String query) {
        counter.increment(query);
        
        // Check if trending (popular in last hour)
        if (counter.getCount(query, Duration.ofHours(1)) > TRENDING_THRESHOLD) {
            // Boost this query in autocomplete
            autocompleteService.boostQuery(query, TRENDING_BOOST);
        }
    }
}

class SlidingWindowCounter {
    private Map<String, TreeMap<Long, Integer>> counts; // query -> timestamp -> count
    
    public void increment(String query) {
        long now = System.currentTimeMillis();
        counts.computeIfAbsent(query, k -> new TreeMap<>())
            .merge(now, 1, Integer::sum);
    }
    
    public int getCount(String query, Duration window) {
        long cutoff = System.currentTimeMillis() - window.toMillis();
        return counts.getOrDefault(query, new TreeMap<>())
            .tailMap(cutoff)
            .values()
            .stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
}
```

**Q3: How do you scale to millions of QPS?**

1. **Caching**: Redis cache for popular prefixes
2. **Sharding**: Partition Trie by first character (a-z → 26 shards)
3. **CDN**: Serve autocomplete from edge locations
4. **Precomputation**: Compute top suggestions offline, serve from cache
5. **Read Replicas**: Multiple Trie copies for reads

**Q4: How do you handle multi-language support?**

```java
class MultiLanguageTrie {
    private Map<String, Trie> tries; // language -> Trie
    
    public List<String> getSuggestions(String prefix, String language) {
        Trie trie = tries.get(language);
        if (trie == null) trie = tries.get("en"); // Fallback to English
        return trie.getSuggestions(prefix);
    }
}
```

**Q5: How would you implement category-specific search?**

```java
class CategoryTrie {
    private Map<String, Trie> categoryTries; // "books", "electronics", etc.
    
    public List<String> getSuggestions(String prefix, String category) {
        Trie trie = categoryTries.get(category);
        return trie != null ? trie.getSuggestions(prefix) : Collections.emptyList();
    }
}
```

## Architecture (Distributed)

```
User → CDN → Load Balancer → Autocomplete Service → Redis Cache
                                      ↓
                                  Trie Cluster (sharded)
                                      ↓
                               Query Log (Kafka)
                                      ↓
                            Analytics Pipeline (Spark)
                                      ↓
                         Update Trie (daily batch job)
```

## Optimization: Caching Strategy

```java
class CachingStrategy {
    private Cache hotCache;    // Top 1000 prefixes (in-memory)
    private Cache warmCache;   // Top 100K prefixes (Redis)
    
    public List<String> get(String prefix) {
        // L1: In-memory
        List<String> result = hotCache.get(prefix);
        if (result != null) return result;
        
        // L2: Redis
        result = warmCache.get(prefix);
        if (result != null) {
            hotCache.set(prefix, result); // Promote to L1
            return result;
        }
        
        // L3: Trie
        result = trie.getSuggestions(prefix);
        warmCache.set(prefix, result);
        return result;
    }
}
```

## Time/Space Complexity

| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(L) where L = query length | O(N × L) where N = # queries |
| Search | O(L + K) where K = # suggestions | O(1) with caching |
| Update frequency | O(L × K) to update all nodes | - |

## Key Topics
- Trie data structure for prefix matching
- Caching top K suggestions at each node
- Fuzzy matching (edit distance)
- Trending queries (sliding window)
- Personalization (user history)
- Scaling (sharding, caching, CDN)
- Real-time vs batch updates
