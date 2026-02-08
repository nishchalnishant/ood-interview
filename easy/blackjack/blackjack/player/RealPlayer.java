package blackjack.player;

// Represents a real player in the Blackjack game
public class RealPlayer implements Player {
    private final String name;
    private final Hand hand;
    private int bet;
    private int balance;

    // Constructor for RealPlayer
    public RealPlayer(String name, int startBalance) {
        this.name = name;
        this.hand = new Hand();
        this.bet = 0;
        this.balance = startBalance;
    }

    // Places a bet for the player
    @Override
    public void bet(int bet) {
        if(bet > balance) {
            throw new IllegalArgumentException("Bet is greater than balance");
        }
        this.bet = bet;
        this.balance -= bet;
    }

    // Handles the player losing a bet
    @Override
    public void loseBet() {
        this.bet = 0;
    }

    // Handles returning the player's bet
    @Override
    public void returnBet() {
        this.balance += bet;
        this.bet = 0;
    }

    // Handles the player winning a payout
    @Override
    public void payout() {
        this.balance += bet * 2;  // Return bet plus equal amount
        this.bet = 0;
    }

    // Checks if the player is bust
    @Override
    public boolean isBust() {
        return hand.isBust();
    }

    // Returns the player's hand
    @Override
    public Hand getHand() {
        return hand;
    }

    // Returns the player's balance
    @Override
    public int getBalance() {
        return balance;
    }

    // Returns the player's name
    @Override
    public String getName() {
        return name;
    }

    // Returns the player's current bet
    @Override
    public int getBet() {
        return bet;
    }

    // getter methods are omitted for brevity
}
