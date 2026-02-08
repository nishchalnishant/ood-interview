// Represents a deep-dive version of the BlackJackGame for advanced logic
public class BlackJackGame {

    // ... fields unchanged ...

    // Finds the next eligible player who can take an action
    public Player getNextEligiblePlayer() {
        // No current player: find first eligible player from the start
        if (currentPlayer == null) {
            for (Player player : players) {
                if (!Action.STAND.equals(playerTurnStatusMap.get(player)) && !player.isBust()) {
                    currentPlayer = player;
                    return currentPlayer;
                }
            }
            // Instead of calling dealerTurn(), check if the dealer can act
            if (!Action.STAND.equals(playerTurnStatusMap.get(dealer))) {
                currentPlayer = dealer;
                return dealer;
            }
        } else {
            int currentIndex = players.indexOf(currentPlayer);
            for (int i = currentIndex + 1; i < players.size(); i++) {
                Player player = players.get(i);
                if (!Action.STAND.equals(playerTurnStatusMap.get(player)) && !player.isBust()) {
                    currentPlayer = player;
                    return currentPlayer;
                }
            }
            // If all players are done, check if the dealer can act
            if (currentPlayer != dealer && !Action.STAND.equals(playerTurnStatusMap.get(dealer))) {
                currentPlayer = dealer;
                return dealer;
            }
        }
        return null;  // All turns are complete, including the dealer's
    }


    // Executes the next turn by acting for the next player or dealer
    public void playNextTurn() {
        Player nextPlayer = getNextEligiblePlayer();
        if (nextPlayer != null) {
            performPlayerAction(nextPlayer);
        }
    }


    // Performs the action decided by the player's decision logic
    public void performPlayerAction(Player player) {
        Action action = player.getDecisionLogic().decideAction(player.getHand());
        if (action == Action.HIT) {
            hit(player);
        } else if (action == Action.STAND) {
            stand(player);
        }
    }


    // ... other methods unchanged, dealerTurn() removed ...
}
