# Social Network (Facebook/Twitter) - LLD Interview Guide

## Problem Statement

Design a social network platform like Facebook or Twitter that allows users to create profiles, connect with friends/followers, post content, view personalized news feeds, and interact with posts through likes and comments.

## Clarifying Questions

1. **Scale**: How many users? DAU (Daily Active Users)? Posts per day?
2. **Features**: Which features? (profiles, friends, posts, news feed, likes, comments, groups, messages?)
3. **Type**: Facebook-like (bidirectional friends) or Twitter-like (unidirectional follow)?
4. **News Feed**: Chronological or algorithm-based ranking?
5. **Media**: Text only or images/videos too?
6. **Real-time**: Real-time updates or eventual consistency acceptable?
7. **Privacy**: Public/private profiles? Post visibility controls?

## Core Requirements

### Functional Requirements
- User registration and authentication
- User profiles (name, bio, profile picture)
- Friend connections (Facebook) or Follow/Followers (Twitter)
- Create, edit, delete posts
- Like and comment on posts
- News feed generation (personalized for each user)
- Search users
- Privacy settings (public, friends-only, private)
- Notifications

### Non-Functional Requirements
- **Scalability**: Handle millions of users
- **Performance**: News feed loads < 2 seconds
- **Availability**: 99.99% uptime
- **Consistency**: Eventual consistency acceptable for news feed
- **Low Latency**: Real-time or near real-time updates

## Key Classes (Core OOD)

| Class | Responsibility |
|-------|---------------|
| `User` | User profile, authentication credentials |
| `Post` | Content, author, timestamp, privacy |
| `Comment` | Comment text, author, parent post |
| `Like` | User who liked, target (post/comment) |
| `Friendship` | Friend relationship (bidirectional or unidirectional) |
| `NewsFeed` | Aggregated posts for a user |
| `NewsFeedGenerator` | Algorithm to generate personalized feed |
| `NotificationService` | Send notifications for likes, comments, friend requests |
| `PrivacyManager` | Control post visibility |

## Design Patterns

1. **Factory Pattern**: Create different post types (text, photo, video, shared)
2. **Observer Pattern**: Notify followers when user posts
3. **Strategy Pattern**: Different news feed algorithms (chronological, ranked, ML-based)
4. **Composite Pattern**: Posts can contain comments, which are also content
5. **Facade Pattern**: Simplified API for complex operations (post creation with media upload)

## High-Level Architecture

```
┌─────────────┐
│   Client    │
│ (Web/Mobile)│
└──────┬──────┘
       │
┌──────▼──────────────────────────┐
│     API Gateway / Load Balancer │
└──────┬──────────────────────────┘
       │
┌──────▼──────────┐
│  Application    │
│    Servers      │
│  (Microservices)│
└─────┬───────────┘
      │
┌─────▼─────────────────────────────────┐
│ ┌──────────┐  ┌──────────┐  ┌───────┐│
│ │User      │  │Post      │  │News   ││
│ │Service   │  │Service   │  │Feed   ││
│ │          │  │          │  │Service││
│ └──────────┘  └──────────┘  └───────┘│
└───────────────────────────────────────┘
      │
┌─────▼──────────────────────────────┐
│     Data Layer                     │
│ ┌────────┐  ┌────────┐  ┌────────┐│
│ │User DB │  │Post DB │  │Graph DB││
│ │(SQL)   │  │(NoSQL) │  │(Neo4j) ││
│ └────────┘  └────────┘  └────────┘│
│ ┌────────┐  ┌────────────────────┐│
│ │Cache   │  │Message Queue       ││
│ │(Redis) │  │(Kafka/RabbitMQ)    ││
│ └────────┘  └────────────────────┘│
└────────────────────────────────────┘
```

## Implementation Details

### Core Classes

```java
class User {
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private String bio;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
    private PrivacySettings privacySettings;
    
    // Relations
    private Set<String> friendIds;      // For Facebook model
    private Set<String> followerIds;    // For Twitter model
    private Set<String> followingIds;   // For Twitter model
}

class Post {
    private String postId;
    private String authorId;
    private String content;
    private PostType type; // TEXT, PHOTO, VIDEO, LINK
    private List<String> mediaUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Privacy privacy; // PUBLIC, FRIENDS, PRIVATE
    
    private Set<String> likedByUserIds;
    private List<Comment> comments;
    
    public int getLikeCount() {
        return likedByUserIds.size();
    }
    
    public boolean canViewPost(String viewerId) {
        if (privacy == Privacy.PUBLIC) return true;
        if (privacy == Privacy.PRIVATE) return viewerId.equals(authorId);
        // FRIENDS: check if viewer is friend of author
        return friendshipService.areFriends(authorId, viewerId);
    }
}

class Comment {
    private String commentId;
    private String postId;
    private String authorId;
    private String content;
    private LocalDateTime createdAt;
    private Set<String> likedByUserIds;
}

class Friendship {
    private String userId1;
    private String userId2;
    private FriendshipStatus status; // PENDING, ACCEPTED, BLOCKED
    private LocalDateTime createdAt;
}
```

### News Feed Generation

**Approach 1: Pull Model (On-Demand)**
- User requests feed → fetch recent posts from all friends
- Query: `SELECT * FROM posts WHERE author_id IN (user's friends) ORDER BY created_at DESC LIMIT 100`

**Pros**: Always fresh, no storage overhead
**Cons**: Slow for users with many friends (O(N) queries)

**Approach 2: Push Model (Fan-out on Write)**
- When user posts → push to all followers' feeds immediately
- Each user has pre-generated feed in cache/database

**Pros**: Fast read (O(1)), pre-computed
**Cons**: Expensive writes for celebrity users (millions of followers)

**Approach 3: Hybrid (Best)**
- **Regular users**: Fan-out on write
- **Celebrity users** (>1M followers): Fan-out on read
- Cache aggressively

```java
class NewsFeedGenerator {
    private final PostService postService;
    private final FriendshipService friendshipService;
    private final CacheService cacheService;
    
    public List<Post> generateFeed(String userId, int limit) {
        // Check cache first
        List<Post> cachedFeed = cacheService.getFeed(userId);
        if (cachedFeed != null) return cachedFeed;
        
        // Get user's friends/following
        Set<String> friendIds = friendshipService.getFriends(userId);
        

        // Fetch recent posts from friends
        List<Post> posts = postService.getPostsByAuthors(friendIds, limit * 2);
        
        // Filter by privacy settings
        posts = posts.stream()
            .filter(post -> post.canViewPost(userId))
            .collect(Collectors.toList());
        
        // Rank/sort posts (chronological or algorithm)
        posts = rankPosts(posts, userId);
        
        // Limit results
        posts = posts.stream().limit(limit).collect(Collectors.toList());
        
        // Cache result
        cacheService.cacheFeed(userId, posts, Duration.ofMinutes(10));
        
        return posts;
    }
    
    private List<Post> rankPosts(List<Post> posts, String userId) {
        // Simple: chronological
        return posts.stream()
            .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
            .collect(Collectors.toList());
        
        // Advanced: ML-based ranking considering:
        // - Recency, engagement, user's past interactions, etc.
    }
}
```

## Common Interview Questions

**Q1: How do you scale the news feed for millions of users?**

1. **Caching**: Redis cache for user feeds (10-15 min TTL)
2. **Database Sharding**: Partition users/posts by user_id hash
3. **CDN**: Serve media (images/videos) from CDN
4. **Asynchronous**: Generate feeds asynchronously using message queues
5. **Read Replicas**: Multiple database replicas for read-heavy operations
6. **Denormalization**: Store post count, like count directly (avoid joins)

**Q2: How do you handle celebrity users with millions of followers?**

- **Don't fan-out on write** for celebrities
- Use **fan-out on read** (pull model) for their followers
- **Cache** celebrity posts aggressively
- **Threshold**: Automatically detect users >100K followers and switch strategy

**Q3: How do you implement real-time notifications?**

```java
class NotificationService {
    private final MessageQueue messageQueue; // Kafka
    private final WebSocketManager wsManager;
    
    public void notifyLike(String postAuthorId, String likerId, String postId) {
        Notification notif = new Notification(
            postAuthorId, 
            NotificationType.LIKE, 
            likerId + " liked your post"
        );
        
        // Option 1: WebSocket (real-time)
        if (wsManager.isUserOnline(postAuthorId)) {
            wsManager.send(postAuthorId, notif);
        }
        
        // Option 2: Push notification (mobile)
        pushNotificationService.send(postAuthorId, notif);
        
        // Option 3: Store in DB for later
        notificationRepository.save(notif);
    }
}
```

**Q4: How do you handle privacy and security?**

- **Authentication**: OAuth 2.0 / JWT tokens
- **Authorization**: Check privacy settings before showing posts
- **Encryption**: HTTPS for all traffic, encrypt sensitive data at rest
- **Rate Limiting**: Prevent spam, DDoS attacks
- **Content Moderation**: AI/ML to detect inappropriate content

**Q5: How would you implement friend suggestions?**

```java
class FriendSuggestionService {
    public List<User> suggestFriends(String userId) {
        // Algorithm 1: Mutual friends
        Set<String> friends = friendshipService.getFriends(userId);
        Map<String, Integer> mutualCount = new HashMap<>();
        
        for (String friendId : friends) {
            Set<String> friendsOfFriend = friendshipService.getFriends(friendId);
            for (String candidate : friendsOfFriend) {
                if (!candidate.equals(userId) && !friends.contains(candidate)) {
                    mutualCount.merge(candidate, 1, Integer::sum);
                }
            }
        }
        
        // Algorithm 2: Similar interests (graph algorithms, ML)
        // Algorithm 3: Location-based, workplace, school
        
        return mutualCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .map(e -> userService.getUser(e.getKey()))
            .collect(Collectors.toList());
    }
}
```

## Database Schema

### SQL (User Data)
```sql
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    bio TEXT,
    profile_picture_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

CREATE TABLE friendships (
    friendship_id VARCHAR(36) PRIMARY KEY,
    user_id1 VARCHAR(36) NOT NULL,
    user_id2 VARCHAR(36) NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'BLOCKED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id1) REFERENCES users(user_id),
    FOREIGN KEY (user_id2) REFERENCES users(user_id),
    INDEX idx_user1 (user_id1),
    INDEX idx_user2 (user_id2)
);
```

### NoSQL (Posts - Cassandra/MongoDB)
```javascript
{
    post_id: "uuid",
    author_id: "user_uuid",
    content: "text",
    media_urls: ["url1", "url2"],
    created_at: "timestamp",
    privacy: "PUBLIC",
    like_count: 1234,
    comment_count: 56,
    liked_by: ["user1", "user2", ...] // or separate table
}
```

### Graph Database (Friend Network - Neo4j)
```cypher
// Users as nodes
CREATE (u:User {user_id: 'uuid', username: 'john'})

// Friendship as relationship
MATCH (u1:User {user_id: 'abc'}), (u2:User {user_id: 'xyz'})
CREATE (u1)-[:FRIENDS_WITH]->(u2)

// Find mutual friends
MATCH (me:User {user_id: 'abc'})-[:FRIENDS_WITH]-(friend)-[:FRIENDS_WITH]-(suggested)
WHERE NOT (me)-[:FRIENDS_WITH]-(suggested) AND me <> suggested
RETURN suggested, COUNT(friend) as mutual_count
ORDER BY mutual_count DESC
LIMIT 10
```

## Testing Strategy

- **Unit Tests**: Individual services (PostService, FriendshipService)
- **Integration Tests**: News feed generation, privacy filtering
- **Load Tests**: Simulate millions of users, posts per second
- **A/B Testing**: Test feed algorithms for engagement

## Common Pitfalls

1. Not considering celebrity users (hotspots)
2. Ignoring cache invalidation strategy
3. Not sharding/partitioning data
4. Synchronous feed generation (slow)
5. Not handling privacy correctly
6. Storing everything in SQL (use NoSQL for posts)
7. Not using CDN for media

## Real-World Considerations

### Scalability
- **Horizontal Scaling**: Add more application servers
- **Database Sharding**: Partition by user_id
- **Microservices**: Separate services for users, posts, feed, notifications
- **Message Queues**: Kafka for async processing (feed generation, notifications)

### Performance
- **Caching Layers**: Redis for feeds, user data, friend lists
- **CDN**: CloudFront/Akamai for static content
- **Database Indexing**: Index on user_id, created_at
- **Lazy Loading**: Load images as user scrolls

### Monitoring
- Track: Feed generation time, post latency, error rates
- Alerts: If feed generation > 2s, database CPU > 80%
- Logging: ELK stack (Elasticsearch, Logstash, Kibana)

---

**Key Interview Topics:**
- Pull vs Push vs Hybrid feed generation
- Handling celebrity users
- Database choice (SQL vs NoSQL vs Graph)
- Caching strategy
- Scaling to millions of users
- Privacy and security
- Real-time notifications (WebSockets, Push)
