package blackjack.player;

// Interface representing a player in Blackjack
public interface Player {
    // Places a bet for the player
    void bet(int bet);

    // Handles the player losing a bet
    void loseBet();

    // Handles returning the player's bet
    void returnBet();

    // Handles the player winning a payout
    void payout();

    // Checks if the player is bust
    boolean isBust();

    // Returns the player's hand
    Hand getHand();

    // Returns the player's balance
    int getBalance();

    // Returns the player's name
    String getName();

    // Returns the player's current bet
    int getBet();
}
