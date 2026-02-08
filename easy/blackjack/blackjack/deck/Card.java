package blackjack.deck;

// Represents a playing card with a rank and suit
public class Card {
    public final Rank rank;
    public final Suit suit;

    // Constructor for Card
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }
 
    // Returns the possible values for the card's rank
    public int[] getRankValues() {
        return rank.getRankValues();
    }

    // Returns a string representation of the card
    @Override
    public String toString() {
        return "Card{" + "rank=" + rank + ", suit=" + suit + '}';
    }
}
