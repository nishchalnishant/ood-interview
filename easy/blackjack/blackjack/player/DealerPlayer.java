package blackjack.player;

// Represents the dealer in the Blackjack game
public class DealerPlayer implements Player {
    private final String name = "Dealer";
    private final Hand hand;

    // Constructor for DealerPlayer
    public DealerPlayer() {
        this.hand = new Hand();
    }

    // Dealer does not bet, so this method is empty
    @Override
    public void bet(int bet) {
        // Dealer does not bet, so this method is empty
    }

    // Dealer does not lose bets, so this method is empty
    @Override
    public void loseBet() {
        // Dealer does not lose bets, so this method is empty
    }

    // Dealer does not return bets, so this method is empty
    @Override
    public void returnBet() {
        // Dealer does not return bets, so this method is empty
    }

    // Dealer does not get a payout, so this method only prints the winning hand
    @Override
    public void payout() {
        // Player won with card value
    }

    // Checks if the dealer is bust
    @Override
    public boolean isBust() {
        return hand.isBust();
    }

    // Returns the dealer's hand
    @Override
    public Hand getHand() {
        return hand;
    }

    // Returns the dealer's balance (always 0)
    @Override
    public int getBalance() {
        return 0;
    }

    // Returns the dealer's name
    @Override
    public String getName() {
        return name;
    }

    // Returns the dealer's bet (always 0)
    @Override
    public int getBet() {
        return 0;
    }
}
