# Blackjack - LLD Interview Guide

## Problem Statement

Design a Blackjack card game system that supports multiple players, dealer logic, card dealing, hand evaluation, betting, and game flow management. The system should handle standard Blackjack rules including hits, stands, busts, and natural blackjacks.

## Clarifying Questions

1. **Players**: How many players? Single vs dealer or multiplayer?
2. **Betting**: Do we need to implement betting and chips management?
3. **Rules**: Standard rules (split, double down, insurance) or simplified?
4. **Deck**: Single deck or multiple decks (shoe)?
5. **Card Counting**: Should we prevent card counting detection?
6. **UI**: Console-based or should we design for GUI integration?

## Core Requirements

### Functional Requirements
- Deck management (shuffle, deal)
- Player and Dealer hands
- Hit, Stand, Double Down actions
- Calculate hand values (Ace as 1 or 11)
- Determine winners (closest to 21, blackjack, bust)
- Betting system (optional but common)
- Multiple rounds/games

### Non-Functional Requirements
- Fair and random card dealing
- Clear game state representation
- Extensible for adding game variants

## Key Classes

| Class | Responsibility |
|-------|---------------|
| `BlackJackGame` | Orchestrates game flow, manages rounds |
| `Deck` | Manages 52 cards, shuffling, dealing |
| `Card` | Represents a single card (rank, suit) |
| `Hand` | Collection of cards, calculates value |
| `Player` / `RealPlayer` | Player with hand and chips |
| `DealerPlayer` | Dealer with specific rules (hit on 16, stand on 17) |

## Design Patterns

1. **Strategy Pattern**: Different player strategies (dealer vs player rules)
2. **Factory Pattern**: Creating different types of players
3. **State Pattern**: Game states (betting, dealing, playing, resolving)
4. **Template Method**: Common game flow with customizable steps

## Common Interview Questions

**Q1: How do you handle Ace value (1 or 11)?**
- Calculate hand with all Aces as 11
- If bust, convert Aces to 1 one at a time until not bust or no Aces left
- Keep track of "soft" hands (hand with Ace counted as 11)

```java
public int getValue() {
    int value = cards.stream().mapToInt(Card::getValue).sum();
    int aceCount = (int) cards.stream().filter(c -> c.getRank() == Rank.ACE).count();
    
    // Start with all Aces as 11, then convert to 1 if needed
    while (value > 21 && aceCount > 0) {
        value -= 10; // Convert one Ace from 11 to 1
        aceCount--;
    }
    return value;
}
```

**Q2: How would you implement Split?**
- Check if first two cards have same rank
- Create two new hands from the original
- Deal one additional card to each hand
- Play each hand independently
- Handle double bet amount

**Q3: How do you implement dealer logic?**
- Dealer must hit on 16 or less
- Dealer must stand on 17 or more
- Dealer plays after all players finish
- Automated decision making (no user input)

## Testing Strategy

- Unit tests for hand value calculation (especially Aces)
- Test dealer logic (hits until 17)
- Test win conditions (blackjack, bust, push)
- Test edge cases (multiple Aces, all face cards)

## Common Pitfalls

1. Not handling Aces correctly (soft/hard hands)
2. Not shuffling deck properly (bias)  
3. Allowing invalid actions (hit after stand)
4. Not comparing hands correctly (dealer vs player)
5. Not resetting game state between rounds

---

**Key Interview Topics:**
- Explain hand value algorithm with Aces
- Discuss how to extend for variants (Spanish 21, Pontoon)
- Thread safety for multiplayer online version
- Preventing card counting in online version
