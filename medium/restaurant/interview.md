# Restaurant Reservation/Management System - LLD Interview Guide

## Problem Statement

Design a restaurant management system handling table reservations, seating, order management, and billing.

## Key Requirements

- Table management (different sizes)
- Reservation booking with time slots
- Walk-in customer seating
- Order tracking
- Bill generation

## Key Classes

- `Restaurant`: Main system
- `Table`: Dining table with capacity
- `Reservation`: Booking details
- `Order`: Food order
- `MenuItem`: Menu item with price
- `Bill`: Final invoice

## Common Questions

**Q1: How do you handle overlapping reservations?**
```java
boolean isTableAvailable(Table table, LocalDateTime time, int durationHours) {
    LocalDateTime endTime = time.plusHours(durationHours);
    
    return reservations.stream()
        .filter(r -> r.getTable() == table)
        .noneMatch(r -> timeOverlaps(r.getStartTime(), r.getEndTime(), time, endTime));
}
```

**Q2: How do you optimize table allocation for walk-ins?**
- Group multiple parties at larger tables if needed
- Use smallest available table that fits party size
- Combine adjacent small tables for large groups

## Key Topics
- Time slot management
- Table allocation algorithms
- Waitlist management
- Order state machine (placed → preparing → ready → served)
