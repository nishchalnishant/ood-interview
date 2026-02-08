# Parking Lot System

This is a Java implementation of a parking lot system that manages vehicle parking, spot allocation, and fare calculation.

## Prerequisites

- Java 11 or higher
- JUnit 5 (Jupiter)

## Project Structure

```
parkinglot/
├── fare/           # Fare calculation related classes
├── spot/           # Parking spot management classes
├── vehicle/        # Vehicle related classes
├── ParkingLot.java # Main parking lot class
└── ParkingLotTest.java # Test class
```

## Running the Tests

```bash
# From repository root
./gradlew :parkinglot:test
```

## Test Description

The `ParkingLotTest` class contains a test method `testVehicleJourney()` that verifies:
- Vehicle entry into the parking lot
- Ticket generation
- Finding parked vehicle
- Vehicle exit
- Spot availability after vehicle leaves

## Expected Output

When tests run successfully, you should see:
```
Vehicle ABC123 has left. Total fare: $0.00

╷
├─ JUnit Jupiter ✔
│  └─ ParkingLotTest ✔
│     └─ testVehicleJourney() ✔
...
[         1 tests found           ]
[         0 tests skipped         ]
[         1 tests started         ]
[         0 tests aborted         ]
[         1 tests successful      ]
[         0 tests failed          ]
```

## Troubleshooting

If you encounter any issues:

1. Make sure Java 11 or higher is installed:
   ```bash
   java -version
   ```

2. Verify the JUnit jar is in the correct location

3. Check that all Java files are in the correct package structure

4. Ensure you're running the commands from the project root directory 
