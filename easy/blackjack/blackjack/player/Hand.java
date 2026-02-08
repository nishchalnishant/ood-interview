package blackjack.player;

import blackjack.deck.Card;
import blackjack.deck.Rank;

import java.util.*;

// Represents a player's hand in Blackjack
public class Hand {
    final List<Card> handCards = new ArrayList<>();

    /** Sorted set of all possible hand values, accounting for Ace flexibility (1 or 11). */
    final SortedSet<Integer> possibleValues = new TreeSet<>();

    // Constructor for Hand
    public Hand() {
    }

    /**
     * Adds a card to the hand and updates the set of possible total values.
     * For Aces (1 or 11), computes all combinations with existing totals; for other cards, adds their value to each total.
     */
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add null card to hand");
        }
        handCards.add(card);

        // card.getRankValues() returns [1, 11] for Aces or a single value (e.g., [10]) for others. 
        if (possibleValues.isEmpty()) {
            // Initialize with the card's values
            for (int value : card.getRankValues()) {
                possibleValues.add(value);
            }
        } else {
            // Add all possible card values to each existing total
            SortedSet<Integer> newPossibleValue = new TreeSet<>();
            for (int value : possibleValues) {
                for (int cardValue : card.getRankValues()) {
                    newPossibleValue.add(value + cardValue);
                }
            }
            possibleValues.clear();
            possibleValues.addAll(newPossibleValue);
        }
    }

    // Returns an unmodifiable list of cards in the hand
    public List<Card> getCards() {
        return Collections.unmodifiableList(handCards);
    }

    // Returns an unmodifiable sorted set of possible hand values
    public SortedSet<Integer> getPossibleValues() {
        return Collections.unmodifiableSortedSet(possibleValues);
    }

    // Clears the hand and possible values
    public void clear() {
        handCards.clear();
        possibleValues.clear();
    }

    // Returns a string representation of the hand
    @Override
    public String toString() {
        return "Hand{" +
                "handCards=" + handCards +
                ", possibleValue=" + possibleValues +
                '}';
    }

    // Checks if the hand is bust (all possible values > 21)
    public boolean isBust() {
        // check if all possible value of the player's hand is busted
        if (possibleValues.isEmpty()) {
        return false;
        }
        else {
        return possibleValues.first() > 21;
        }
    }
}
