package blackjack.deepdive;

import blackjack.Action;
import blackjack.Hand;

// Interface representing a player in the deepdive version of Blackjack
public interface Player {
    // Returns the decision logic for the player
    PlayerDecisionLogic getDecisionLogic();
    
    // Returns the player's hand
    Hand getHand();
    
    // Returns true if the player has busted
    boolean isBust();
    
    // Returns the player's name
    String getName();
}
