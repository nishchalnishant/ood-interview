package blackjack.deepdive;

import blackjack.Action;
import blackjack.Hand;

// Implements decision logic for the dealer in Blackjack
public class DealerDecisionLogic implements PlayerDecisionLogic {
    // Decides whether the dealer should hit or stand based on hand value
    @Override
    public Action decideAction(Hand hand) {
        return hand.getPossibleValues().last() < 17 ? Action.HIT : Action.STAND;
    }
}
