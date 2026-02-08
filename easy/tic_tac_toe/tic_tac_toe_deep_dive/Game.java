package tictactoe;

import java.util.Arrays;
import java.util.Optional;

public class Game {
    // Core game components
    private final Board board;              // Manages the game board state
    private final MoveHistory moveHistory;  // Tracks all moves made in the game
    private final ScoreTracker scoreTracker;// Keeps track of player scores
    private Player[] players;               // Array of players in the game
    private int currentPlayerIndex;         // Index of the current player's turn

    // Constructor initializes game components and starts a new game
    public Game(Player playerX, Player playerY) {
        board = new Board();
        moveHistory = new MoveHistory();
        scoreTracker = new ScoreTracker();
        startNewGame(playerX, playerY);
    }

    // Resets the game state and initializes players for a new game
    public void startNewGame(Player playerX, Player playerY) {
        board.reset();
        moveHistory.clearHistory();
        players = new Player[]{playerX, playerY};
        currentPlayerIndex = 0;
    }

    // Processes a player's move, validates it, and updates game state
    public void makeMove(int colIndex, int rowIndex, Player player) {
        // Validate that game hasn't ended
        if (getGameStatus().equals(GameCondition.ENDED)) {
            throw new IllegalStateException("game ended");
        }
        // Validate that it's the correct player's turn
        if (players[currentPlayerIndex] != player) {
            throw new IllegalArgumentException("not the current player");
        }
        // Validate that the position is not already taken
        if(board.getPlayerAt(colIndex, rowIndex) != null) {
            throw new IllegalArgumentException("board position is taken");
        }
        // Update the board with the player's move
        board.updateBoard(colIndex, rowIndex, player);
        // Record the move in history
        final Move newMove = new Move(colIndex, rowIndex, player);
        moveHistory.recordMove(newMove);
        // Switch to the next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        // If game has ended, update the score
        if (getGameStatus().equals(GameCondition.ENDED)) {
            scoreTracker.reportGameResult(players[0], players[1], board.getWinner());
        }
    }

    // Reverts the last move made in the game
    public void undoMove() {
        // Check if game has ended to prevent undoing after winner is reported
        if (getGameStatus().equals(GameCondition.ENDED)) {
            throw new IllegalStateException("game ended and winner already reported");
        }
        // Get the last move from history
        final Move lastMove = moveHistory.undoMove();

        // Update current player index to previous player
        if (currentPlayerIndex == 0) {
            currentPlayerIndex = players.length - 1;
        }
        else {
            currentPlayerIndex--;
        }

        // Clear the board position of the undone move
        board.updateBoard(lastMove.getColIndex(), lastMove.getRowIndex(), null);
    }

    // Determines if the game is in progress or has ended
    public GameCondition getGameStatus() {
        Optional<Player> winner = board.getWinner();
        if (winner.isPresent()) {
            return GameCondition.ENDED;
        }
        return board.isFull() ? GameCondition.ENDED : GameCondition.IN_PROGRESS;
    }

    // Returns a string representation of the current game state
    public String getStateString() {
        return "Between players " + Arrays.deepToString(players) + ", state: " + getGameStatus();
    }

    // Returns the player whose turn it is
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    // Prints the current state of the game for visualization
    public void printGameState() {
        // Printing Game State
        // Between players [Player1, Player2], state: IN_PROGRESS
        // Current Player: Player1
    }

    // Returns the score tracker for accessing game statistics
    public ScoreTracker getScoreTracker() {
        return scoreTracker;
    }
}
