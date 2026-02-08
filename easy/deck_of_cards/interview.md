# Deck of Cards - LLD Interview Guide

## Problem Statement

Design a generic Deck of Cards that can be used as the foundation for various card games (Poker, Blackjack, etc.). The deck should support shuffling, dealing, and standard 52-card deck operations.

## Key Requirements

- Standard 52-card deck (13 ranks × 4 suits)
- Support for shuffling (randomization)
- Deal cards one at a time
- Track remaining cards
- Reshuffle capability
- Extensible for different games

## Key Classes

```java
enum Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum Rank {
    ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), 
    SEVEN(7), EIGHT(8), NINE(9), TEN(10), 
    JACK(10), QUEEN(10), KING(10);
    
    private final int value;
    Rank(int value) { this.value = value; }
    public int getValue() { return value; }
}

class Card {
    private final Rank rank;
    private final Suit suit;
    
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }
    
    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }
    
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

class Deck {
    private List<Card> cards;
    private int currentIndex; // For dealing
    
    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
        currentIndex = 0;
    }
    
    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
        currentIndex = 0;
    }
    
    public Card deal() {
        if (currentIndex >= cards.size()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.get(currentIndex++);
    }
    
    public int remainingCards() {
        return cards.size() - currentIndex;
    }
    
    public void reset() {
        currentIndex = 0;
        initializeDeck();
    }
}
```

## Common Interview Questions

**Q1: How would you implement custom shuffling algorithm (Fisher-Yates)?**
```java
public void shuffle() {
    Random random = new Random();
    for (int i = cards.size() - 1; i > 0; i--) {
        int j = random.nextInt(i + 1);
        Collections.swap(cards, i, j);
    }
    currentIndex = 0;
}
```

**Q2: How would you extend this for multiple decks (e.g., Blackjack with 6 decks)?**
```java
class MultiDeck extends Deck {
    public MultiDeck(int numDecks) {
        super();
        cards.clear();
        for (int i = 0; i < numDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(rank, suit));
                }
            }
        }
    }
}
```

**Q3: How would you handle Jokers?**
- Add `JOKER` to `Rank` enum
- Or create separate `Joker` class
- Add parameter to Deck constructor: `new Deck(boolean includeJokers)`

## Design Patterns

- **Template Method**: Base `Deck` with extensible initialization
- **Factory**: Creating different deck types (standard, with jokers, multiple decks)
- **Immutability**: `Card` is immutable (final fields)

## Key Topics
- Enum usage for Suit and Rank
- Fisher-Yates shuffle algorithm
- Immutability for Card class
- Extensibility for different card games
