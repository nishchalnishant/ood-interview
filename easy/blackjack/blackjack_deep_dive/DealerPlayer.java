// Represents the dealer player in the deepdive version of Blackjack
public class DealerPlayer implements Player {
    private final PlayerDecisionLogic decisionLogic;

    // Constructor for DealerPlayer
    public DealerPlayer() {
        this.hand = new Hand();
        this.decisionLogic = new DealerDecisionLogic();
    }

    // Returns the decision logic for the dealer
    @Override
    public PlayerDecisionLogic getDecisionLogic() {
        return decisionLogic;
    }
    // ... other methods ...
}
