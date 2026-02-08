# Elevator System - LLD Interview Guide

## Problem Statement

Design an elevator control system for a building with multiple elevators. The system should efficiently handle passenger requests, optimize elevator movement, and minimize wait times.

## Clarifying Questions

1. **Building**: How many floors? How many elevators?
2. **Capacity**: Max people/weight per elevator?
3. **Algorithm**: FCFS, nearest car, zoned elevators?
4. **Features**: Express elevators? Service/freight elevators? Emergency mode?
5. **Requests**: Only hall buttons or in-car buttons too?
6. **Direction**: Can change direction mid-ride or must complete direction?

## Core Requirements

### Functional Requirements
- Multiple elevators serving multiple floors
- Handle up/down hall button requests
- Handle in-car floor button requests
- Efficient elevator dispatch algorithm
- Track elevator status (idle, moving up/down, door open/closed)
- Handle multiple simultaneous requests
- Prevent overcapacity

### Non-Functional Requirements
- Minimize average wait time
- Minimize energy consumption
- Thread-safe (concurrent requests)
- Real-time responsiveness

## Key Classes

| Class | Responsibility |
|-------|---------------|
| `ElevatorSystem` | Coordinate multiple elevators |
| `ElevatorCar` | Single elevator car with state |
| `ElevatorDispatchController` | Assigns requests to elevators |
| `Request` | Floor request (from hall or car) |
| `Direction` | UP, DOWN, IDLE |
| `ElevatorStatus` | Current state of elevator |
| `HallwayButtonPanel` | Hall call buttons outside elevators |

## Design Patterns

1. **Strategy Pattern**: Different dispatch strategies (FCFS, shortest-seek-time, scan)
2. **Observer Pattern**: Notify elevator status displays
3. **State Pattern**: Elevator states (idle, moving, doors-open)
4. **Singleton**: Elevator system controller

## Common Interview Questions

**Q1: What elevator dispatch algorithms can you use?**

1. **First-Come-First-Served (FCFS)**
   - Process requests in order received
   - Simple but inefficient

2. **Shortest Seek Time First (SSTF)**
   - Assign request to the nearest available elevator
   - Similar to disk scheduling
   ```java
   ElevatorCar findOptimalElevator(Request request) {
       return elevators.stream()
           .filter(e -> e.canService(request))
           .min(Comparator.comparingInt(e ->
               Math.abs(e.getCurrentFloor() - request.getFloor())))
           .orElse(null);
   }
   ```

3. **SCAN (Elevator Algorithm)**
   - Elevator continues in one direction until no more requests
   - Then reverses direction
   - Prevents starvation

4. **LOOK**
   - Like SCAN but only goes as far as last request in direction

**Q2: How do you handle concurrent requests?**
```java
class ElevatorDispatchController {
    private final Queue<Request> requestQueue = new ConcurrentLinkedQueue<>();
    
    public synchronized void addRequest(Request request) {
        requestQueue.offer(request);
        dispatchRequest(request);
    }
    
    private void dispatchRequest(Request request) {
        ElevatorCar bestElevator = findOptimalElevator(request);
        if (bestElevator != null) {
            bestElevator.assignRequest(request);
        }
    }
}
```

**Q3: How does an elevator decide its next move?**
```java
class ElevatorCar implements Runnable {
    private Direction currentDirection = Direction.IDLE;
    private int currentFloor = 1;
    private Set<Integer> upRequests = new TreeSet<>();
    private Set<Integer> downRequests = new TreeSet<>(Collections.reverseOrder());
    
    public void run() {
        while (true) {
            if (currentDirection == Direction.UP) {
                processUpRequests();
            } else if (currentDirection == Direction.DOWN) {
                processDownRequests();
            } else {
                // Idle: pick next direction based on pending requests
                determineNextDirection();
            }
        }
    }
    
    private void processUpRequests() {
        Integer nextFloor = upRequests.higher(currentFloor);
        if (nextFloor != null) {
            moveTo(nextFloor);
            openDoors();
            upRequests.remove(nextFloor);
        } else {
            currentDirection = Direction.IDLE;
        }
    }
}
```

**Q4: How would you add priority/express elevators?**
- Add `priority` field to `Request`
- VIP/Express elevator only serves penthouse/executive floors
- Separate dispatch logic for express vs regular
- Use `PriorityQueue` instead of FIFO queue

**Q5: How handle emergency situations (fire, power outage)?**
- Emergency mode: All elevators go to ground floor and open doors
- Disable all requests except firefighter control
- Use generator power for controlled descent
- Fire elevators stay operational for firefighters

## Implementation Highlights

### Request Assignment
```java
void assignRequest(Request request) {
    if (request.getDirection() == Direction.UP || 
        (currentDirection == Direction.UP && request.getFloor() >= currentFloor)) {
        upRequests.add(request.getFloor());
    } else {
        downRequests.add(request.getFloor());
    }
}
```

### Capacity Management
```java
class ElevatorCar {
    private int currentWeight = 0;
    private static final int MAX_WEIGHT = 1000; // kg
    
    boolean canAcceptPassenger(int weight) {
        return currentWeight + weight <= MAX_WEIGHT;
    }
    
    void addPassenger(int weight) {
        if (!canAcceptPassenger(weight)) {
            throw new OverCapacityException();
        }
        currentWeight += weight;
    }
}
```

## Testing Strategy

- Unit tests for dispatch algorithm (SSTF, SCAN)
- Test concurrent requests from multiple floors
- Test edge cases (all elevators busy, opposite directions)
- Stress test with high load
- Test emergency mode

## Common Pitfalls

1. Not handling requests in optimal order (starvation)
2. Racing conditions with concurrent requests
3. Not considering elevator capacity
4. Ignoring direction (assigning down request to up-moving elevator)
5. Not implementing timeout for door close
6. Integer overflow for floor numbers (use appropriate data types)

## Real-World Considerations

- **Energy Optimization**: Group nearby requests, idle on middle floors
- **Maintenance Mode**: Take elevator out of service
- **Analytics**: Track avg wait time, peak hours, usage patterns
- **IoT**: Real-time monitoring, predictive maintenance
- **Accessibility**: Voice announcements, braille buttons, wheelchair access

---

**Key Topics:**
- Dispatch algorithms (SSTF vs SCAN)
- Concurrency and thread safety
- State management
- Preventing starvation
- Optimization for wait time vs energy
