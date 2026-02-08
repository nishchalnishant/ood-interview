# Movie Ticket Booking System

This is a Java-based movie ticket booking system that allows users to manage cinemas, movies, screenings, and ticket bookings.

## Project Structure
```
movie_ticket/
├── MovieBookingSystem.java
├── MovieBookingSystemTest.java
├── location/
├── rate/
├── showing/
└── ticket/
```

## Running the Tests

```bash
# From repository root
./gradlew :movieticket:test
```

### What the Tests Cover

The test suite (`MovieBookingSystemTest.java`) verifies the core functionality of the booking system:
- Creating a cinema with rooms and seating layouts
- Adding movies and screenings
- Managing seat availability
- Booking tickets
- Verifying ticket prices

### Test Output

A successful test run will show output similar to:
```
Test run finished after XX ms
[         4 containers found      ]
[         0 containers skipped    ]
[         4 containers started    ]
[         0 containers aborted    ]
[         4 containers successful ]
[         0 containers failed     ]
[         1 tests found           ]
[         0 tests skipped         ]
[         1 tests started         ]
[         0 tests aborted         ]
[         1 tests successful      ]
[         0 tests failed          ]
```

### Troubleshooting

If you encounter any issues:
1. Make sure Java is installed and properly configured
2. Check that you have write permissions in the `/tmp` directory
3. Verify your internet connection
4. If you get compilation errors, make sure you're in the root directory of the project 
