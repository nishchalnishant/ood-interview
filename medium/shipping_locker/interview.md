# Shipping/Package Locker System - LLD Interview Guide

## Problem Statement

Design an automated package locker system (like Amazon Lockers) where customers can pick up deliveries from self-service lockers at convenient locations.

## Key Requirements

- Multiple locker sizes (small, medium, large)
- Package delivery and pickup workflow
- Access code/OTP generation
- Locker assignment algorithm
- Time-based pickup windows
- Automatic return to sender if not picked up

## Key Classes

- `LockerSystem`: Main system controller
- `Locker`: Individual compartment
- `Package`: Delivery package
- `LockerSize`: Enum (SMALL, MEDIUM, LARGE)
- `Notification`: Send pickup codes to customers
- `Transaction`: Delivery/pickup record

## Common Questions

**Q1: How do you assign packages to lockers?**
```java
Locker assignLocker(Package package) {
    LockerSize requiredSize = determineSize(package);
    
    return lockers.stream()
        .filter(l -> l.getSize() == requiredSize && l.isAvailable())
        .findFirst()
        .orElseThrow(() -> new NoAvailableLockerException());
}

LockerSize determineSize(Package package) {
    if (package.getDimensions() <= SMALL_THRESHOLD) return LockerSize.SMALL;
    if (package.getDimensions() <= MEDIUM_THRESHOLD) return LockerSize.MEDIUM;
    return LockerSize.LARGE;
}
```

**Q2: How do you generate and validate pickup codes?**
```java
class Package {
    private String pickupCode;
    private LocalDateTime expiresAt;
    
    String generatePickupCode() {
        pickupCode = generateRandomCode(6); // 6-digit code
        expiresAt = LocalDateTime.now().plusDays(3); // 3-day pickup window
        return pickupCode;
    }
    
    boolean validateCode(String code) {
        return pickupCode.equals(code) && LocalDateTime.now().isBefore(expiresAt);
    }
}
```

**Q3: What happens if package isn't picked up in time?**
- Background job checks expiry daily
- Mark package as "expired"
- Open locker remotely for retrieval agent  
- Return to sender or move to central facility
- Free up locker for new packages

**Q4: How do you handle multiple deliveries for same customer?**
- **Option 1**: Assign to separate lockers
- **Option 2**: Combine in one larger locker if possible
- Customer gets multiple pickup codes or one code for all

## Key Topics
- Locker assignment optimization
- Code generation and security
- Expiry and auto-cleanup mechanisms
- Notification system (SMS/Email)
- Hardware integration (locker doors, keypad)
