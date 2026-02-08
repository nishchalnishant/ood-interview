package blackjack.deepdive;

import blackjack.Action;
import blackjack.Hand;

// Interface for player decision logic in the deepdive version
public interface PlayerDecisionLogic {
    // Returns the action the player should take based on their hand
    Action decideAction(Hand hand);
}
