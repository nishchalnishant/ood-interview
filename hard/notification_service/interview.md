# Notification Service - LLD Interview Guide

## Problem Statement

Design a distributed notification service that can send notifications through multiple channels (email, SMS, push notifications, in-app) with priorities, rate limiting, retry logic, and template management.

## Core Requirements

### Functional
- Send notifications via multiple channels (email, SMS, push, in-app)
- Priority-based delivery (critical, high, medium, low)
- Template management with variable substitution
- User preferences (opt-in/opt-out per channel)
- Batch notifications
- Scheduled notifications
- Retry failed deliveries
- Track delivery status

### Non-Functional
- **Scalability**: Handle millions of notifications/day
- **Reliability**: 99.9% delivery rate
- **Performance**: < 1 second for critical notifications
- **Rate Limiting**: Respect provider limits (Twilio, SendGrid)

## Key Components

```java
enum NotificationChannel {
    EMAIL, SMS, PUSH, IN_APP, WEBHOOK
}

enum Priority {
    CRITICAL,  // Immediate (security alerts)
    HIGH,      // < 1 min (OTPs)
    MEDIUM,    // < 5 min (order updates)
    LOW        // Best effort (marketing)
}

class Notification {
    private String notificationId;
    private String userId;
    private NotificationChannel channel;
    private Priority priority;
    private String templateId;
    private Map<String, String> variables;
    private LocalDateTime scheduledAt;
    private int retryCount;
    private NotificationStatus status;
}

class NotificationService {
    private final Map<NotificationChannel, NotificationProvider> providers;
    private final PriorityQueue<Notification> priorityQueue;
    private final RateLimiter rateLimiter;
    private final TemplateEngine templateEngine;
    
    public void sendNotification(Notification notification) {
        // 1. Check user preferences
        if (!canSendToUser(notification)) {
            notification.setStatus(NotificationStatus.OPTED_OUT);
            return;
        }
        
        // 2. Render template
        String content = templateEngine.render(
            notification.getTemplateId(), 
            notification.getVariables()
        );
        
        // 3. Add to priority queue
        priorityQueue.offer(notification);
        
        // 4. Process async
        processQueue();
    }
    
    private void processQueue() {
        while (!priorityQueue.isEmpty()) {
            Notification notification = priorityQueue.poll();
            
            // Rate limiting
            if (!rateLimiter.allowRequest(notification.getChannel())) {
                // Requeue
                schedule(notification, Duration.ofSeconds(1));
                continue;
            }
            
            // Send via provider
            NotificationProvider provider = providers.get(notification.getChannel());
            try {
                provider.send(notification, content);
                notification.setStatus(NotificationStatus.SENT);
            } catch (Exception e) {
                handleFailure(notification, e);
            }
        }
    }
    
    private void handleFailure(Notification notification, Exception e) {
        if (notification.getRetryCount() < MAX_RETRIES) {
            notification.incrementRetryCount();
            // Exponential backoff
            long delay = (long) Math.pow(2, notification.getRetryCount());
            schedule(notification, Duration.ofSeconds(delay));
        } else {
            notification.setStatus(NotificationStatus.FAILED);
            logFailure(notification, e);
        }
    }
}

// Provider abstraction
interface NotificationProvider {
    void send(Notification notification, String content);
}

class EmailProvider implements NotificationProvider {
    private final SendGridClient sendGrid;
    public void send(Notification n, String content) {
        sendGrid.sendEmail(getUserEmail(n.getUserId()), content);
    }
}

class SMSProvider implements NotificationProvider {
    private final TwilioClient twilio;
    public void send(Notification n, String content) {
        twilio.sendSMS(getUserPhone(n.getUserId()), content);
    }
}

class PushProvider implements NotificationProvider {
    private final FCMClient fcm; // Firebase Cloud Messaging
    public void send(Notification n, String content) {
        String deviceToken = getUserDeviceToken(n.getUserId());
        fcm.sendPush(deviceToken, content);
    }
}
```

## Common Interview Questions

**Q1: How do you handle rate limits from providers?**

Use Token Bucket per provider:
```java
class ProviderRateLimiter {
    // SendGrid: 100 emails/sec, Twilio: 10 SMS/sec
    private Map<NotificationChannel, TokenBucket> limiters;
    
    public ProviderRateLimiter() {
        limiters.put(NotificationChannel.EMAIL, new TokenBucket(100, 1)); // 100/sec
        limiters.put(NotificationChannel.SMS, new TokenBucket(10, 1));    // 10/sec
    }
    
    public boolean allowRequest(NotificationChannel channel) {
        return limiters.get(channel).allowRequest();
    }
}
```

**Q2: How do you implement priority-based delivery?**

```java
class PriorityQueue<Notification> implements Queue<Notification> {
    private final Queue<Notification> criticalQueue = new LinkedList<>();
    private final Queue<Notification> highQueue = new LinkedList<>();
    private final Queue<Notification> mediumQueue = new LinkedList<>();
    private final Queue<Notification> lowQueue = new LinkedList<>();
    
    public void offer(Notification n) {
        switch (n.getPriority()) {
            case CRITICAL: criticalQueue.offer(n); break;
            case HIGH: highQueue.offer(n); break;
            case MEDIUM: mediumQueue.offer(n); break;
            case LOW: lowQueue.offer(n); break;
        }
    }
    
    public Notification poll() {
        if (!criticalQueue.isEmpty()) return criticalQueue.poll();
        if (!highQueue.isEmpty()) return highQueue.poll();
        if (!mediumQueue.isEmpty()) return mediumQueue.poll();
        return lowQueue.poll();
    }
}
```

**Q3: How do you handle template management?**

```java
class TemplateEngine {
    private final Map<String, String> templates; // Cache
    
    public String render(String templateId, Map<String, String> variables) {
        String template = templates.get(templateId);
        // "Hello {{name}}, your OTP is {{otp}}"
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        return template;
    }
}
```

## Architecture (Distributed)

```
Producer Services → Kafka → Notification Workers → Providers (SendGrid/Twilio/FCM)
                             ↓
                          Redis (rate limit)
                             ↓
                          PostgreSQL (audit log)
```

## Key Topics
- Multi-channel delivery
- Priority queues
- Rate limiting per provider
- Retry with exponential backoff
- Template rendering
- User preferences and opt-out
- Delivery tracking and analytics
