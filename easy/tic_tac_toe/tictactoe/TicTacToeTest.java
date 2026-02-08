package tictactoe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TicTacToeTest {
    @Test
    public void testPlay() {
        System.out.println("\n=== Testing Tic-Tac-Toe Game System: Complete Game Play ===");
        
        System.out.println("\n--- Creating Players ---");
        Player playerX = new Player("X", 'X');
        Player playerY = new Player("Y", 'Y');
        System.out.println("✓ Created Player X (symbol: X)");
        System.out.println("✓ Created Player Y (symbol: Y)");

        System.out.println("\n--- Initializing Game ---");
        Game game = new Game(playerX, playerY);
        System.out.println("✓ New tic-tac-toe game started");
        assertEquals(GameCondition.IN_PROGRESS, game.getGameStatus());
        assertEquals(playerX, game.getCurrentPlayer());
        System.out.println("✓ Game status: IN_PROGRESS");
        System.out.println("✓ Current player: Player X (first to move)");

        System.out.println("\n--- Making Game Moves ---");
        System.out.println("✓ Player X moves at position (0,0) - Top-left corner");
        game.makeMove(0,0, game.getCurrentPlayer());
        assertEquals(playerY, game.getCurrentPlayer());
        System.out.println("✓ Turn switched to Player Y");

        System.out.println("✓ Player Y moves at position (1,0) - Middle-left");
        game.makeMove(1,0, game.getCurrentPlayer());
        System.out.println("✓ Player X moves at position (0,1) - Top-center");
        game.makeMove(0,1, game.getCurrentPlayer());
        System.out.println("✓ Player Y moves at position (1,1) - Center");
        game.makeMove(1,1, game.getCurrentPlayer());
        System.out.println("✓ Player X moves at position (0,2) - Top-right corner");
        game.makeMove(0,2, game.getCurrentPlayer());

        System.out.println("\n--- Game Completion ---");
        assertEquals(GameCondition.ENDED, game.getGameStatus());
        System.out.println("✓ Game status: ENDED");
        System.out.println("✓ Player X wins with a horizontal line in the top row!");
        System.out.println("✓ Final game board:");
        game.printGameState();

        System.out.println("\n--- Score Tracking ---");
        ScoreTracker scoreTracker = game.getScoreTracker();
        assertEquals(1, scoreTracker.getPlayerRatings().get(playerX));
        System.out.println("✓ Player X rating: " + scoreTracker.getPlayerRatings().get(playerX) + " point");
        System.out.println("✓ Player Y rating: " + scoreTracker.getPlayerRatings().get(playerY) + " points");

        assertEquals(1, scoreTracker.getRank(playerX));
        assertEquals(2, scoreTracker.getRank(playerY));
        assertEquals(2, scoreTracker.getTopPlayers().size());
        System.out.println("✓ Player rankings:");
        System.out.println("  - Player X rank: " + scoreTracker.getRank(playerX) + " (Winner)");
        System.out.println("  - Player Y rank: " + scoreTracker.getRank(playerY) + " (Runner-up)");
        System.out.println("  - Total players tracked: " + scoreTracker.getTopPlayers().size());
        System.out.println("=== Tic-Tac-Toe Game Test Completed Successfully ===\n");
    }

    @Test
    public void testDuplicatePlay() {
        System.out.println("\n=== Testing Tic-Tac-Toe: Duplicate Move Validation ===");
        
        System.out.println("\n--- Setting Up Game ---");
        Player playerX = new Player("X", 'X');
        Player playerY = new Player("Y", 'Y');
        Game game = new Game(playerX, playerY);
        System.out.println("✓ New game started with Player X and Player Y");

        System.out.println("\n--- Making First Move ---");
        System.out.println("✓ Player X moves at position (1,1) - Center");
        game.makeMove(1,1, game.getCurrentPlayer());
        System.out.println("✓ Move successful - center position occupied");

        System.out.println("\n--- Attempting Duplicate Move ---");
        System.out.println("✓ Player Y attempts to move at position (1,1) - Already occupied");
        try {
            game.makeMove(1,1, game.getCurrentPlayer());
            fail("didn't catch IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Duplicate move correctly rejected:");
            System.out.println("  - Expected behavior: Cannot place move on occupied position");
        }
        System.out.println("=== Duplicate Move Test Completed Successfully ===\n");
    }
}
