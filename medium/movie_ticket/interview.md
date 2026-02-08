# Movie Ticket Booking System - LLD Interview Guide

## Problem Statement

Design a movie ticket booking system for cinema theaters that allows users to browse movies, select showtimes, choose seats, and book tickets with appropriate pricing.

## Key Requirements

- Multiple cinemas, each with multiple screens
- Movies with multiple showtimes (screenings)
- Seat selection with different pricing (Normal, Premium, VIP)
- Booking management and payment
- Prevent double-booking (concurrency)

## Key Classes

| Class | Responsibility |
|---|---|
| `MovieBookingSystem` | Main orchestrator |
| `Cinema` | Theater location |
| `Room` | Individual screen/hall |
| `Movie` | Film details |
| `Screening` | Specific showtime for a movie |
| `Seat` | Individual seat with type |
| `Order` | Booking with selected seats |
| `PriceRate` | Pricing strategy (Normal, Premium, VIP) |

## Design Patterns

1. **Strategy Pattern**: `PriceRate` interface for different pricing tiers
2. **Factory Pattern**: Creating seats with layouts
3. **Singleton Pattern**: `MovieBookingSystem` centralized controller
4. **Observer Pattern**: Notify users of booking confirmations

## Common Questions

**Q1: How do you prevent two users from booking the same seat?**
- **Optimistic Locking**: Check seat availability before confirming
- **Pessimistic Locking**: Lock seat for 10 minutes during selection
- **Atomic Operations**: Use database transactions with SELECT FOR UPDATE

```java
public synchronized boolean bookSeats(Screening screening, List<Seat> seats) {
    // Check all seats still available
    if (seats.stream().allMatch(Seat::isAvailable)) {
        seats.forEach(seat -> seat.setOccupied(true));
        return true;
    }
    return false;
}
```

**Q2: How do you handle seat holds (temporary reservations)?**
```java
class Seat {
    private boolean occupied = false;
    private LocalDateTime holdExpiresAt = null;
    
    public boolean isAvailable() {
        if (occupied) return false;
        if (holdExpiresAt != null && LocalDateTime.now().isBefore(holdExpiresAt)) {
            return false; // Held by another user
        }
        return true;
    }
    
    public void hold(int minutes) {
        holdExpiresAt = LocalDateTime.now().plusMinutes(minutes);
    }
}
```

**Q3: How do you calculate ticket prices?**
```java
interface PriceRate {
    BigDecimal getPrice();
}

class PremiumRate implements PriceRate {
    public BigDecimal getPrice() { return new BigDecimal("15.00"); }
}

class Order {
    BigDecimal calculateTotal() {
        return seats.stream()
            .map(seat -> seat.getSeatType().getPriceRate().getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

## Key Topics
- Concurrency control for seat booking
- Seat hold/timeout mechanism
- Pricing strategies
- Database schema for cinemas, screens, showtimes
