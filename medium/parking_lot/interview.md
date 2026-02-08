# Parking Lot - LLD Interview Guide

## Problem Statement

Design a parking lot system that manages vehicle parking, tracks available spots, calculates fees, and handles entry/exit of vehicles with different types of spots for different vehicle sizes.

## Clarifying Questions

1. **Size**: How many levels? How many spots per level?
2. **Spot Types**: Compact, regular, large, handicapped, electric vehicle spots?
3. **Vehicle Types**: Motorcycle, car, truck, bus?
4. **Pricing**: Flat rate, hourly, daily max? Different rates for vehicle types?
5. **Payment**: Cash, card, prepaid, monthly passes?
6. **Features**: Spot reservation? Real-time availability display? License plate recognition?
7. **Capacity**: Single entrance/exit or multiple?

## Core Requirements

### Functional Requirements
- Multiple levels with multiple spots per lev
- Different spot types (compact, regular, large, handicapped)
- Different vehicle types (motorcycle, car, van, truck)
- Vehicle assignment to appropriate spot
- Track available/occupied spots
- Calculate parking fee based on duration
- Issue parking ticket on entry
- Process payment and allow exit
- Display available spots

### Non-Functional Requirements
- High availability (parking lot operates 24/7)
- Fast spot allocation (< 1 second)
- Accurate fee calculation
- Thread-safe for concurrent vehicles

## Key Classes

| Class | Responsibility |
|-------|---------------|
| `ParkingLot` | Singleton, manages levels and overall operations |
| `ParkingLevel` | Represents one level, contains spots |
| `ParkingSpot` | Abstract base for spot types |
| `CompactSpot`, `RegularSpot`,`OversizedSpot`, `HandicappedSpot` | Concrete spot types |
| `Vehicle` | Abstract vehicle class |
| `Motorcycle`, `Car`, `Truck` | Concrete vehicle types |
| `ParkingTicket` | Generated on entry, tracks entry time |
| `FeeCalculator` | Calculates parking fees |
| `ParkingManager` | Manages spot allocation logic |

## Design Patterns

1. **Singleton Pattern**: `ParkingLot` (single parking lot instance)
2. **Factory Pattern**: Creating different spot/vehicle types
3. **Strategy Pattern**: Different fee calculation strategies (hourly, daily, monthly)
4. **Observer Pattern**: Notify display boards of spot availability

### Class Hierarchy
```
ParkingSpot (abstract)
 ├── CompactSpot
 ├── RegularSpot
 ├── OversizedSpot
 └── HandicappedSpot

Vehicle (abstract)
 ├── Motorcycle
 ├── Car
 └── Truck
```

## Common Interview Questions

**Q1: How do you assign vehicles to appropriate spots?**

Spot sizing rules:
- Motorcycle → Can park in any spot (compact, regular, large)
- Car → Regular or large spots  
- Truck → Only large/oversized spots

Algorithm:
```java
public ParkingSpot findSpot(Vehicle vehicle) {
    for (ParkingLevel level : levels) {
        for (ParkingSpot spot : level.getSpots()) {
            if (spot.canFitVehicle(vehicle) && spot.isAvailable()) {
                return spot;
            }
        }
    }
    return null; // No spot available
}
```

**Q2: How do you handle concurrent vehicles trying to park?**
- **Synchronized allocation**: Lock the spot during assignment
```java
public synchronized boolean parkVehicle(Vehicle vehicle) {
    ParkingSpot spot = findSpot(vehicle);
    if (spot != null && spot.isAvailable()) {
        spot.assignVehicle(vehicle);
        return true;
    }
    return false;
}
```
- Or use **database with atomic operations** (optimistic/pessimistic locking)
- Or use **distributed lock** (Redis, ZooKeeper) for multiple entry points

**Q3: How do you calculate parking fees?**
```java
interface FeeCalculator {
    BigDecimal calculateFee(Duration parkingDuration, VehicleType type);
}

class HourlyFeeCalculator implements FeeCalculator {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("5.00");
    
    public BigDecimal calculateFee(Duration duration, VehicleType type) {
        long hours = duration.toHours();
        if (duration.toMinutesPart() > 0) hours++; // Round up
        
        BigDecimal fee = HOURLY_RATE.multiply(BigDecimal.valueOf(hours));
        
        // Apply multipliers for vehicle type
        if (type == VehicleType.TRUCK) {
            fee = fee.multiply(new BigDecimal("2.0")); // 2x for trucks
        }
        return fee;
    }
}
```

**Q4: How would you add spot reservation?**
- Add `reservationId` and `reservedUntil` fields to `ParkingSpot`
- Modify `isAvailable()`: `return !occupied && (reservationId == null || reservedUntil.isBefore(now))`
- Add `reserveSpot(vehicleId, duration)` method
- Release reservation if not claimed within time window

**Q5: How would you implement "find my car" feature?**
- Store `licensePlate → spotId` mapping in HashMap
- Add cameras at entry to capture license plate (LPR - License Plate Recognition)
- API: `findCar(licensePlate)` returns spot location
- Mobile app integration with indoor navigation

## Implementation Highlights

### Parking Ticket
```java
class ParkingTicket {
    private final String ticketId;
    private final LocalDateTime entryTime;
    private final Vehicle vehicle;
    private final ParkingSpot assignedSpot;
    
    public Duration getParkingDuration() {
        return Duration.between(entryTime, LocalDateTime.now());
    }
}
```

### Entry/Exit Flow
```java
// Entry
public ParkingTicket entry(Vehicle vehicle) {
    ParkingSpot spot = parkingManager.findAndAssignSpot(vehicle);
    if (spot == null) {
        throw new ParkingFullException("No available spots");
    }
    ParkingTicket ticket = new ParkingTicket(vehicle, spot);
    tickets.put(ticket.getId(), ticket);
    return ticket;
}

// Exit
public BigDecimal exit(String ticketId, Payment payment) {
    ParkingTicket ticket = tickets.get(ticketId);
    Duration duration = ticket.getParkingDuration();
    BigDecimal fee = feeCalculator.calculateFee(duration, ticket.getVehicle().getType());
    
    if (payment.getAmount().compareTo(fee) < 0) {
        throw new InsufficientPaymentException();
    }
    
    ticket.getAssignedSpot().removeVehicle();
    tickets.remove(ticketId);
    return payment.getAmount().subtract(fee); // Return change
}
```

## Testing Strategy

- Unit tests for spot assignment logic
- Test fee calculation for various durations
- Test concurrent parking (multiple threads)
- Test "parking lot full" scenario
- Test different vehicle-spot combinations
- Test edge cases (parking for 0 minutes, parking for 30 days)

## Common Pitfalls

1. Not handling concurrency (race conditions on spot assignment)
2. Using wrong data type for money (use BigDecimal)
3. Not rounding up partial hours in fee calculation
4. Not validating vehicle-spot compatibility
5. Memory leak (not removing exited tickets)
6. Not handling "parking lot full" gracefully

## Scaling & Extensions

**Q: How would you scale to 10,000 spots?**
- Use **database** instead of in-memory storage
- Index on `spotId`, `levelId`, `isAvailable`
- Cache available spot count per level/type
- Use **message queue** for entry/exit events

**Q: How would you add monthly passes?**
- Add `PassCard` class with `passId`, `userId`, `expiryDate`
- Store in database
- Check pass validity before allowing entry
- No fee calculation for pass holders

**Q: How would you add EV charging spots?**
- Create `EVChargingSpot extends ParkingSpot`
- Add `chargingRate` field
- Track energy consumed
- Calculate fee = parking fee + electricity fee
- Monitor charging status (ready, charging, completed)

## Real-World Considerations

- **IoT Integration**: Sensors to detect spot occupancy in real-time
- **Payment Gateway**: Integrate with Stripe/Square for card payments
- **Mobile App**: Allow users to find, reserve, and pay via app
- **Analytics**: Heat maps of utilization, peak hours, revenue reports
- **Security**: CCTV integration, emergency call buttons
- **Accessibility**: Handicapped spots near elevators/exits

---

**Key Topics:**
- Inheritance hierarchy for vehicles and spots
- Concurrent spot allocation strategies
- Fee calculation with BigDecimal
- Singleton vs dependency injection for ParkingLot
- Database design for persistence
