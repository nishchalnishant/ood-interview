package blackjack.deepdive;

import blackjack.Action;
import blackjack.Hand;

// Implements decision logic for a real player in Blackjack
public class RealPlayerDecisionLogic implements PlayerDecisionLogic {
    // Decides whether the real player should hit or stand based on hand value
    @Override
    public Action decideAction(Hand hand) {
        return hand.getPossibleValues().last() < 16 ? Action.HIT : Action.STAND;
    }
}
