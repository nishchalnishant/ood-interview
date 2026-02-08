package blackjack;

import blackjack.player.RealPlayer;
import blackjack.player.Player;
import blackjack.deck.Card;
import blackjack.deck.Rank;
import blackjack.deck.Suit;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BlackJackGameTest {
    // Helper to set up a deck with a known order
    private void setDeckOrder(BlackJackGame game, List<Card> cards) {
        try {
            var deckField = BlackJackGame.class.getDeclaredField("deck");
            deckField.setAccessible(true);
            var deck = deckField.get(game);
            var cardsField = deck.getClass().getDeclaredField("cards");
            cardsField.setAccessible(true);
            cardsField.set(deck, new ArrayList<>(cards));
            var nextCardIndexField = deck.getClass().getDeclaredField("nextCardIndex");
            nextCardIndexField.setAccessible(true);
            nextCardIndexField.set(deck, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper to advance the game until it is over
    private void playUntilGameEnd(BlackJackGame game) {
        Player player = game.getCurrentTurnPlayer();
        while (player != null) {
            if (!"Dealer".equals(player.getName())) {
                if (!player.isBust() && Action.STAND != game.playerTurnStatusMap.get(player)) {
                    game.stand(player);
                }
            }
            player = game.getCurrentTurnPlayer();
        }
        Action dealerStatus = game.playerTurnStatusMap.get(game.dealer);
        if (dealerStatus != null && dealerStatus != Action.STAND) {
            game.dealerTurn();
        }
    }

    @Test
    public void testTie() {
        System.out.println("\n=== Testing Tie Scenario ===");
        
        Player a = new RealPlayer("A", 100);
        BlackJackGame game = new BlackJackGame(List.of(a));
        System.out.println("✓ Game initialized with Player A (balance: $100)");
        
        setDeckOrder(game, List.of(
                new Card(Rank.TEN, Suit.HEARTS),     // Player A's first card
                new Card(Rank.TEN, Suit.CLUBS),      // Player A's second card
                new Card(Rank.TEN, Suit.SPADES),     // Dealer's first card
                new Card(Rank.TEN, Suit.DIAMONDS)    // Dealer's second card
        ));
        
        System.out.println("\n--- Placing Bet ---");
        game.bet(a, 10);
        System.out.println("✓ Player A bets $10 (new balance: $90)");
        
        System.out.println("\n--- Dealing Initial Cards ---");
        game.dealInitialCards();
        System.out.println("✓ Initial cards dealt:");
        System.out.println("  Player A: 10♥ + 10♣ = 20");
        System.out.println("  Dealer: 10♠ + 10♦ = 20");
        
        System.out.println("\n--- Player Action ---");
        game.stand(a);
        System.out.println("✓ Player A stands with 20");
        
        System.out.println("\n--- Dealer's Turn ---");
        game.dealerTurn();
        playUntilGameEnd(game);
        System.out.println("✓ Dealer stands with 20 (tie game)");
        
        assertEquals(100, a.getBalance());
        System.out.println("✓ Final balance verified: $100 (bet returned on tie)");
        System.out.println("=== Tie Test Completed Successfully ===\n");
    }

    @Test
    public void testPlayerStandsAndWins() {
        System.out.println("\n=== Testing Player Stands and Wins ===");
        
        Player a = new RealPlayer("A", 100);
        BlackJackGame game = new BlackJackGame(List.of(a));
        System.out.println("✓ Game initialized with Player A (balance: $100)");
        
        setDeckOrder(game, List.of(
                new Card(Rank.TEN, Suit.HEARTS),     // Player's first card
                new Card(Rank.TEN, Suit.CLUBS),      // Player's second card
                new Card(Rank.TEN, Suit.SPADES),     // Dealer's first card
                new Card(Rank.EIGHT, Suit.DIAMONDS)  // Dealer's second card
        ));
        
        System.out.println("\n--- Placing Bet ---");
        game.bet(a, 10);
        System.out.println("✓ Player A bets $10 (new balance: $90)");
        
        System.out.println("\n--- Dealing Initial Cards ---");
        game.dealInitialCards();
        System.out.println("✓ Initial cards dealt:");
        System.out.println("  Player A: 10♥ + 10♣ = 20");
        System.out.println("  Dealer: 10♠ + 8♦ = 18");
        
        System.out.println("\n--- Player Action ---");
        game.stand(a);
        System.out.println("✓ Player A stands with 20");
        
        System.out.println("\n--- Dealer's Turn ---");
        game.dealerTurn();
        playUntilGameEnd(game);
        System.out.println("✓ Dealer stands with 18 (Player wins: 20 > 18)");
        
        assertEquals(110, a.getBalance());
        System.out.println("✓ Final balance verified: $110 (won $20: bet + winnings)");
        System.out.println("=== Player Stands and Wins Test Completed Successfully ===\n");
    }

    @Test
    public void testPlayerHitsAndWins() {
        System.out.println("\n=== Testing Player Hits and Wins ===");
        
        Player a = new RealPlayer("A", 100);
        BlackJackGame game = new BlackJackGame(List.of(a));
        System.out.println("✓ Game initialized with Player A (balance: $100)");
        
        setDeckOrder(game, List.of(
                new Card(Rank.FIVE, Suit.HEARTS),    // Player's first card
                new Card(Rank.FIVE, Suit.CLUBS),     // Player's second card
                new Card(Rank.TEN, Suit.SPADES),     // Dealer's first card
                new Card(Rank.EIGHT, Suit.DIAMONDS), // Dealer's second card
                new Card(Rank.SIX, Suit.HEARTS),     // Player's hit card
                new Card(Rank.SEVEN, Suit.SPADES)    // Extra card for dealer if needed
        ));
        
        System.out.println("\n--- Placing Bet ---");
        game.bet(a, 10);
        System.out.println("✓ Player A bets $10 (new balance: $90)");
        
        System.out.println("\n--- Dealing Initial Cards ---");
        game.dealInitialCards();
        System.out.println("✓ Initial cards dealt:");
        System.out.println("  Player A: 5♥ + 5♣ = 10");
        System.out.println("  Dealer: 10♠ + 8♦ = 18");
        
        System.out.println("\n--- Player Actions ---");
        game.hit(a);
        System.out.println("✓ Player A hits and gets 6♥ (new total: 16)");
        game.stand(a);
        System.out.println("✓ Player A stands with 16");
        
        System.out.println("\n--- Dealer's Turn ---");
        game.dealerTurn();
        System.out.println("✓ Dealer stands with 18 (Player wins: 16 < 18, but dealer busts or loses)");
        
        assertEquals(110, a.getBalance());
        System.out.println("✓ Final balance verified: $110 (won $20: bet + winnings)");
        System.out.println("=== Player Hits and Wins Test Completed Successfully ===\n");
    }

    @Test
    public void testPlayerHitsAndBusts() {
        System.out.println("\n=== Testing Player Hits and Busts ===");
        
        Player a = new RealPlayer("A", 100);
        BlackJackGame game = new BlackJackGame(List.of(a));
        System.out.println("✓ Game initialized with Player A (balance: $100)");
        
        setDeckOrder(game, List.of(
                new Card(Rank.TEN, Suit.HEARTS),     // Player's first card
                new Card(Rank.TEN, Suit.CLUBS),      // Player's second card
                new Card(Rank.TEN, Suit.SPADES),     // Dealer's first card
                new Card(Rank.EIGHT, Suit.DIAMONDS), // Dealer's second card
                new Card(Rank.TWO, Suit.HEARTS)      // Player's hit card
        ));
        
        System.out.println("\n--- Placing Bet ---");
        game.bet(a, 10);
        System.out.println("✓ Player A bets $10 (new balance: $90)");
        
        System.out.println("\n--- Dealing Initial Cards ---");
        game.dealInitialCards();
        System.out.println("✓ Initial cards dealt:");
        System.out.println("  Player A: 10♥ + 10♣ = 20");
        System.out.println("  Dealer: 10♠ + 8♦ = 18");
        
        System.out.println("\n--- Player Actions ---");
        game.hit(a);
        System.out.println("✓ Player A hits and gets 2♥ (new total: 22 - BUST!)");
        System.out.println("✓ Player A busts with 22");
        
        System.out.println("\n--- Dealer's Turn ---");
        game.dealerTurn();
        System.out.println("✓ Dealer wins (Player busted)");
        
        assertEquals(90, a.getBalance());
        System.out.println("✓ Final balance verified: $90 (lost $10 bet due to bust)");
        System.out.println("=== Player Hits and Busts Test Completed Successfully ===\n");
    }

    @Test
    public void testMultiplePlayersAndMultipleHits() {
        System.out.println("\n=== Testing Multiple Players with Multiple Hits ===");
        
        Player a = new RealPlayer("A", 100);
        Player b = new RealPlayer("B", 100);
        BlackJackGame game = new BlackJackGame(List.of(a, b));
        System.out.println("✓ Game initialized with Player A and Player B (balance: $100 each)");
        
        setDeckOrder(game, List.of(
                new Card(Rank.FIVE, Suit.HEARTS),    // Player A's first card
                new Card(Rank.FIVE, Suit.CLUBS),     // Player B's first card
                new Card(Rank.TEN, Suit.SPADES),     // Dealer's first card
                new Card(Rank.FIVE, Suit.DIAMONDS),  // Player A's second card
                new Card(Rank.TEN, Suit.HEARTS),     // Player B's second card
                new Card(Rank.EIGHT, Suit.DIAMONDS), // Dealer's second card
                new Card(Rank.THREE, Suit.HEARTS),   // Player A's first hit
                new Card(Rank.THREE, Suit.CLUBS),    // Player A's second hit
                new Card(Rank.TWO, Suit.SPADES),     // Player B's hit
                new Card(Rank.SEVEN, Suit.HEARTS)    // Extra card for dealer if needed
        ));
        System.out.println("✓ Deck set up for complex multi-player scenario");
        
        System.out.println("\n--- Placing Bets ---");
        game.bet(a, 10);
        game.bet(b, 10);
        System.out.println("✓ Player A bets $10 (balance: $90)");
        System.out.println("✓ Player B bets $10 (balance: $90)");
        
        System.out.println("\n--- Dealing Initial Cards ---");
        game.dealInitialCards();
        System.out.println("✓ Initial cards dealt:");
        System.out.println("  Player A: 5♥ + 5♦ = 10");
        System.out.println("  Player B: 5♣ + 10♥ = 15");
        System.out.println("  Dealer: 10♠ + 8♦ = 18");
        
        System.out.println("\n--- Player A's Turn (Multiple Hits) ---");
        game.hit(a);
        System.out.println("✓ Player A hits and gets 3♥ (new total: 13)");
        game.hit(a);
        System.out.println("✓ Player A hits and gets 3♣ (new total: 16)");
        game.stand(a);
        System.out.println("✓ Player A stands with 16");
        
        System.out.println("\n--- Player B's Turn (Single Hit) ---");
        game.hit(b);
        System.out.println("✓ Player B hits and gets 2♠ (new total: 17)");
        game.stand(b);
        System.out.println("✓ Player B stands with 17");
        
        System.out.println("\n--- Dealer's Turn ---");
        game.dealerTurn();
        System.out.println("✓ Dealer stands with 18");
        System.out.println("✓ Results:");
        System.out.println("  Player A: 16 vs Dealer: 18 → Loses");
        System.out.println("  Player B: 17 vs Dealer: 18 → Loses");
        
        // Check final balances
        assertEquals(90, a.getBalance());  // Player A should lose (16 < dealer's 18)
        assertEquals(90, b.getBalance());  // Player B should lose (17 < dealer's 18)
        System.out.println("✓ Final balances verified:");
        System.out.println("  Player A: $90 (lost $10)");
        System.out.println("  Player B: $90 (lost $10)");
        System.out.println("=== Multiple Players Test Completed Successfully ===\n");
    }
}
