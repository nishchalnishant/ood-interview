# Object-Oriented Design Interview Problems

Comprehensive collection of 28 Low-Level Design (LLD) / Object-Oriented Design (OOD) problems commonly asked in SDE2 and SDE3 interviews. All problems are organized by difficulty level and include detailed interview guides.

## 📁 Repository Structure

```
ood-interview/
├── easy/          # 7 fundamental OOD problems
├── medium/        # 13 intermediate system design problems
├── hard/          # 8 complex distributed system designs
└── build.gradle   # Gradle build configuration
```

## 🎯 Problems by Difficulty

### ✅ Easy (7 Problems)

| Problem | Key Concepts | Interview.md |
|---------|-------------|-------------|
| [Tic-Tac-Toe](easy/tic_tac_toe) | Game logic, State management, Winner detection | ✓ |
| [Vending Machine](easy/vending_machine) | Transaction management, Inventory, BigDecimal for money | ✓ |
| [Blackjack](easy/blackjack) | Card games, Hand evaluation, Dealer logic | ✓ |
| [Deck of Cards](easy/deck_of_cards) | Foundation for card games, Shuffling algorithms | ✓ |
| [Library Management](easy/library_management) | CRUD operations, Fine calculation, Search | ✓ |
| [Hotel Booking](easy/hotel_booking) | Room availability, Reservations, Payment | ✓ |
| [Chess Game](easy/chess_game) | Piece movements, Move validation, Check/Checkmate | ✓ |

### 🔶 Medium (13 Problems)

| Problem | Key Concepts | Interview.md |
|---------|-------------|-------------|
| [ATM System](medium/atm) | State pattern, Security, Transaction atomicity | ✓ |
| [Parking Lot](medium/parking_lot) | Inheritance, Spot allocation, Concurrency | ✓ |
| [Elevator System](medium/elevator_system) | Dispatch algorithms, SCAN/SSTF, Concurrency | ✓ |
| [Movie Ticket Booking](medium/movie_ticket) | Seat booking, Concurrency control, Hold mechanism | ✓ |
| [Restaurant Management](medium/restaurant) | Table reservations, Order management, Time slots | ✓ |
| [File Search](medium/file_search) | Indexing, Trie, Inverted index | ✓ |
| [Grocery Store/POS](medium/grocery_store) | Pricing strategies, Promotions, Payment | ✓ |
| [Shipping Locker](medium/shipping_locker) | Locker assignment, OTP generation, Expiry | ✓ |
| [LRU Cache](medium/lru_cache) | HashMap + Doubly Linked List, O(1) operations | ✓ |
| [Meeting Scheduler](medium/meeting_scheduler) | Calendar, Conflict detection, Recurring meetings | ✓ |
| [Ride Sharing](medium/ride_sharing) | Matching algorithms, Location tracking, Pricing | ✓ |
| [Online Auction](medium/online_auction) | Bidding mechanism, Winner determination | ✓ |
| [Splitwise](medium/splitwise) | Expense tracking, Settlement calculation | ✓ |

### 🔴 Hard (8 Problems)

| Problem | Key Concepts | Interview.md |
|---------|-------------|-------------|
| [Social Network](hard/social_network) | News feed, Friend graph, Privacy, Scalability | ✓ |
| [Message Queue](hard/message_queue) | Kafka-like, Pub-Sub, Partitions, Ordering | ✓ |
| [Rate Limiter](hard/rate_limiter) | Token Bucket, Leaky Bucket, Distributed limits | ✓ |
| [Cloud Storage](hard/cloud_storage) | File sync, Version control, Conflict resolution | ✓ |
| [Notification Service](hard/notification_service) | Multi-channel, Priority queue, Rate limiting | ✓ |
| [Trading Platform](hard/trading_platform) | Order matching, Order book, Real-time pricing | ✓ |
| [Search Autocomplete](hard/search_autocomplete) | Trie, Ranking, Caching, Personalization | ✓ |

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (Java 21  LTS recommended)
- Gradle 7.6+ (wrapper included)

### Build All Projects
```bash
git clone <repo-url>
cd ood-interview

# Build all projects
./gradlew buildAll

# Run all tests
./gradlew runAllTests
```

### Test Specific Problems

#### Easy Problems
```bash
./gradlew :easy:tictactoe:test
./gradlew :easy:vendingmachine:test
./gradlew :easy:blackjack:test
```

#### Medium Problems
```bash
./gradlew :medium:atm:test
./gradlew :medium:parkinglot:test
./gradlew :medium:elevator:test
./gradlew :medium:movieticket:test
```

#### Hard Problems
```bash
./gradlew :hard:socialnetwork:test
./gradlew :hard:messagequeue:test
./gradlew :hard:ratelimiter:test
```

## 📚 Interview.md Guide Format

Each problem includes a comprehensive `interview.md` covering:

1. **Problem Statement** - Clear description of requirements
2. **Clarifying Questions** - What to ask the interviewer
3. **Core Requirements** - Functional & non-functional requirements
4. **Key Classes & Responsibilities** - OOD structure
5. **Design Patterns Used** - Which patterns and why
6. **Common Interview Questions** - With detailed answers
7. **Extension Questions** - How to scale or modify
8. **Trade-offs Discussion** - Design decision analysis
9. **Code Walkthrough** - Step-by-step implementation
10. **Testing Strategy** - How to verify correctness
11. **Common Pitfalls** - What NOT to do
12. **Real-World Considerations** - Production-ready aspects

## 🎓 Interview Preparation Tips

### Study Plan by Timeline

**1 Week Before Interview:**
- Focus on **EASY** problems - understand fundamentals
- Master basic OOD principles (SOLID, encapsulation, inheritance)
- Practice explaining your design decisions

**3-5 Days Before:**
- Practice **MEDIUM** problems - these are most common in interviews
- Focus on design patterns: Strategy, Factory, Singleton, Observer, State
- Practice whiteboard coding of key methods

**1-2 Days Before:**
- Review **HARD** problems conceptually - rarely asked to fully implement
- Focus on discussing trade-offs, scalability, distributed systems
- Review your most recent system design projects

### During the Interview

1. **Clarify Requirements** (5 mins)
   - Ask about scale, users, features
   - Clarify functional vs non-functional requirements
   - Identify core vs optional features

2. **High-Level Design** (10 mins)
   - Draw class diagram
   - Identify key classes and relationships
   - Explain design patterns used

3. **Detailed Design** (15 mins)
   - Code critical classes/interfaces
   - Explain important methods
   - Discuss data structures

4. **Discussion** (10 mins)
   - Talk about trade-offs
   - Explain how to extend
   - Discuss scalability and edge cases

## 🔑 Key Design Patterns by Problem

| Pattern | Problems |
|---------|----------|
| **Singleton** | ParkingLot, VendingMachine, ElevatorSystem |
| **Factory** | Vehicle types, ParkingSpot types, Player types |
| **Strategy** | Pricing(Grocery/Parking), FeeCalculation, DispatchAlgorithm |
| **State** | ATM states, Game states, Elevator states |
| **Observer** | Elevator status updates, Notification systems |
| **Facade** | VendingMachine, ATM (hide complexity) |
| **Builder** | Complex object construction |
| **Command** | Undo/Redo in games |

## 💡 Common Interview Topics

### Easy Level Focus
- Basic OOP concepts (encapsulation, inheritance, polymorphism)
- Simple state management
- Input validation
- Basic algorithms (winner detection, hand evaluation)

### Medium Level Focus
- Design patterns (Factory, Strategy, Observer, State)
- Concurrency and thread safety
- Database schema design
- API design
- Pricing/billing logic with BigDecimal

### Hard Level Focus
- Scalability (horizontal vs vertical)
- Distributed systems concepts
- CAP theorem trade-offs
- Caching strategies
- Message queues and async processing
- Database sharding and replication
- Microservices architecture

## 🛠️ Technical Best Practices

### Money Handling
```java
// ✅ ALWAYS use BigDecimal for money
BigDecimal price = new BigDecimal("19.99");
BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));

// ❌ NEVER use float or double
double price = 19.99; // NO! Precision errors
```

### Concurrency
```java
// ✅ Synchronize critical sections
public synchronized boolean bookSeat(Seat seat) {
    if (seat.isAvailable()) {
        seat.setOccupied(true);
        return true;
    }
    return false;
}
```

### Validation
```java
// ✅ Validate before state changes
public void makeMove(int row, int col) {
    validateGameNotEnded();
    validateCorrectPlayer();
    validatePositionAvailable(row, col);
    
    // Now safe to update
    board.update(row, col, currentPlayer);
}
```

## 📖 Additional Resources

- **Book**: "Object-Oriented Design Interview" by ByteByteGo
- **SOLID Principles**: [Clean Code by Robert C. Martin]
- **Design Patterns**: [Gang of Four Design Patterns]
- **System Design**: [System Design Interview by Alex Xu]

## 🤝 Contributing

This repository is primarily for interview preparation. Each problem is self-contained with complete implementation and tests.

## 📝 License

Educational use - MIT License

## ⭐ Interview Success Tips

1. **Think out loud** - interviewer wants to see your thought process
2. **Start simple** - basic version first, then extend
3. **Use real-world examples** - relate to systems you know
4. **Ask questions** - shows you think about requirements
5. **Discuss trade-offs** - every design has pros and cons
6. **Code neatly** - even on whiteboard, structure matters
7. **Test your design** - walk through edge cases
8. **Be honest** - if you don't know, say so and explain your approach

---

**Good luck with your interviews! 🎯**

For questions or suggestions, feel free to open an issue.
