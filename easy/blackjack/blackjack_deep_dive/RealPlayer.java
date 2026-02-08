// Represents a real player in the deepdive version of Blackjack
public class RealPlayer implements Player {
    private final PlayerDecisionLogic decisionLogic;

    // Constructor for RealPlayer
    public RealPlayer(String name, int startBalance) {
        this.name = name;
        this.hand = new Hand();
        this.bet = 0;
        this.balance = startBalance;
        this.decisionLogic = new RealPlayerDecisionLogic();
    }

    // Returns the decision logic for the player
    @Override
    public PlayerDecisionLogic getDecisionLogic() {
        return decisionLogic;
    }
    // ... other methods ...
}
