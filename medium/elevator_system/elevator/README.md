# Elevator System

This is an implementation of an elevator control system that manages elevator operations in a building. The system handles elevator requests, movement, and dispatching using different strategies.

## Project Structure

```
elevator/
├── components/
│   ├── Direction.java         # Enum for elevator direction (UP, DOWN, IDLE)
│   ├── ElevatorCar.java      # Core elevator functionality
│   ├── ElevatorStatus.java   # Status representation of elevator
│   └── HallwayButtonPanel.java # Handles elevator call buttons
├── dispatch/
│   ├── DispatchingStrategy.java        # Interface for elevator selection strategies
│   ├── ElevatorDispatch.java           # Handles elevator dispatching
│   ├── FirstComeFirstServeStrategy.java # Simple dispatching strategy
│   └── ShortestSeekTimeFirstStrategy.java # Optimized dispatching strategy
├── ElevatorSystem.java       # Main elevator system controller
└── ElevatorSystemTest.java   # Test cases for the elevator system
```

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- JUnit 5.8.2 (included in lib directory)

## Running the Tests

```bash
# From repository root
./gradlew :elevator:test
```

## System Features

1. **Basic Elevator Operations**
   - Move up and down
   - Stop at requested floors
   - Track current floor and direction
   - Handle floor requests

2. **Dispatching Strategies**
   - First Come First Serve: Assigns requests to elevators in order
   - Shortest Seek Time First: Optimizes elevator selection based on distance

3. **Status Monitoring**
   - Current floor tracking
   - Direction status (UP, DOWN, IDLE)
   - Destination queue management

## Example Usage

```java
// Create an elevator car starting at floor 1
ElevatorCar car = new ElevatorCar(1);

// Create an elevator system with a dispatching strategy
ElevatorSystem elevatorSystem = new ElevatorSystem(
    List.of(car), 
    new FirstComeFirstServeStrategy()
);

// Request an elevator from floor 3 going up
elevatorSystem.requestElevator(3, Direction.UP);

// Select a destination floor from inside the elevator
elevatorSystem.selectFloor(car, 8);
```

## Testing

The system includes comprehensive tests in `ElevatorSystemTest.java` that verify:
- Elevator movement
- Direction changes
- Floor request handling
- Destination management
- Dispatching logic
