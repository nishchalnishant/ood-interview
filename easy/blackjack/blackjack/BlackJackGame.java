package blackjack;

import blackjack.deck.Card;
import blackjack.deck.Deck;
import blackjack.player.DealerPlayer;
import blackjack.player.Player;

import java.util.*;

public class BlackJackGame {
    private final Deck deck = new Deck();
    private final List<Player> players = new ArrayList<>();
    protected final Player dealer = new DealerPlayer();
    private Player currentPlayer = null;

    // Tracks the current status of each player's turn (e.g., HIT or STAND)
    Map<Player, Action> playerTurnStatusMap = new HashMap<>();
    GamePhase currentPhase = GamePhase.STARTED;

    public BlackJackGame(List<Player> players){
        for (Player player : players) {
            if (player == null) throw new IllegalArgumentException();
            this.players.add(player);
            this.playerTurnStatusMap.put(player, null);
        }
        this.playerTurnStatusMap.put(dealer, null);
        deck.shuffle();  // Shuffle the deck when game starts
    }

    // Determines the next player who can take an action (i.e., has not stood or bust). If the current player is the dealer, it triggers the dealer's turn.
    public Player getNextEligiblePlayer() {
        // If current player hasn't stood or bust, they can continue their turn
        if (currentPlayer != null && 
            !Action.STAND.equals(playerTurnStatusMap.get(currentPlayer)) && 
            !currentPlayer.isBust()) {
            return currentPlayer;
        }

        // Find the first player who hasn't stood or bust
        if (currentPlayer == null) {
            for (Player player : players) {
                if (!Action.STAND.equals(playerTurnStatusMap.get(player)) && !player.isBust()) {
                    currentPlayer = player;
                    return currentPlayer;
                }
            }
        }

        // else, find the next player after the current one who hasn't stood or bust
        int currentPlayerIndex = players.indexOf(currentPlayer);
        for (int i = currentPlayerIndex + 1; i < players.size(); i++) {
            Player player = players.get(i) ;
            if (!Action.STAND.equals(playerTurnStatusMap.get(player)) && !player.isBust()) {
                if (currentPlayer == dealer) {
                    if (!Action.STAND.equals(playerTurnStatusMap.get(dealer)))
                        dealerTurn();
                    return currentPlayer;
                }
                currentPlayer = player;
                return currentPlayer;
            }
        }


        // If no players are left to act, return null
        return null;
    }

    protected void dealerTurn() {
        // Dealer hits if below 17
        while (dealer.getHand().getPossibleValues().last() < 17) {
            Card newDraw = deck.draw();
            dealer.getHand().addCard(newDraw);
        }
        playerTurnStatusMap.put(dealer, Action.STAND);
        checkGameEndCondition();
    }

    public void startNewRound() {
        deck.reset();
        for (Player player : playerTurnStatusMap.keySet()) {
            player.getHand().clear(); // Clear player's hand
        }
        dealer.getHand().clear(); // Clear dealer's hand
        // Reset all turn statuses to null
        playerTurnStatusMap.replaceAll((p, v) -> null);
        currentPlayer = null; // Reset current player
        currentPhase = GamePhase.STARTED;
    }

    public void dealInitialCards() {
        if (!GamePhase.BET_PLACED.equals(currentPhase)) {
            throw new IllegalStateException("All players must bet before dealing");
        }
        // Deal first card to each real player in order
        for (Player player : players) {
            player.getHand().addCard(deck.draw());
        }
        // Deal first card to dealer
        dealer.getHand().addCard(deck.draw());
        // Deal second card to each real player in order
        for (Player player : players) {
            player.getHand().addCard(deck.draw());
        }
        // Deal second card to dealer
        dealer.getHand().addCard(deck.draw());
        currentPhase = GamePhase.INITIAL_CARD_DRAWN;
    }

    public void bet(Player player, int bet) {
        if (!GamePhase.STARTED.equals(currentPhase)) {
            throw new IllegalStateException("Bets must be placed at the start of the round");
        }
        player.bet(bet);
        // Transition to BET_PLACED once all players have bet
        if (players.stream().filter(
                p -> !(p instanceof DealerPlayer)
        ).allMatch(p -> p.getBet() > 0)) {
            currentPhase = GamePhase.BET_PLACED;
        }
    }

    public void hit(Player player) {
        if (Action.STAND.equals(playerTurnStatusMap.get(player))) {
            throw new IllegalStateException("Player has already stood");
        }
        if (player.isBust()) {
            throw new IllegalStateException("Player is already bust");
        }

        Card drawnCard = deck.draw();
        player.getHand().addCard(drawnCard);
        playerTurnStatusMap.put(player, Action.HIT);
    }

    public void stand(Player player) {
        if (Action.STAND.equals(playerTurnStatusMap.get(player))) {
            throw new IllegalStateException("Player has already stood");
        }
        if (player.isBust()) {
            throw new IllegalStateException("Player is already bust");
        }
        playerTurnStatusMap.put(player, Action.STAND);
    }

    /**
     * Checks if the game has ended (all players done), then resolves bets by comparing each player's hand to the dealer's.
     */
    private void checkGameEndCondition() {
        boolean allPlayersDone = players.stream()
                .allMatch(p -> Action.STAND.equals(playerTurnStatusMap.get(p)) || p.isBust());
        if (!allPlayersDone) {
            return;
        }

        int dealerValue = dealer.getHand().getPossibleValues().last();
        boolean dealerBusts = dealer.isBust();

        for (Player player : players) {
            if (player.isBust()) {
                player.loseBet();
            } else {
                int playerValue = player.getHand().getPossibleValues().last();
                if (dealerBusts || playerValue > dealerValue) {
                    player.payout();
                } else if (playerValue == dealerValue) {
                    player.returnBet();
                } else {
                    player.loseBet();
                }
            }
        }
        currentPhase = GamePhase.END;
    }

    public String getStateString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Player player : playerTurnStatusMap.keySet()) {
            stringBuilder
                    .append(player.getName()).append(": ")
                    .append(player.getHand().toString()).append(", ")
                    .append(playerTurnStatusMap.get(player))
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    protected Deck getDeck() {
        return deck;
    }

    public Player getCurrentTurnPlayer() {
        return currentPlayer;
    }
}
