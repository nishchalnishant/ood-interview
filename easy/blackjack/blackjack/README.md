# Blackjack Java Project

This is a simple object-oriented Blackjack game implemented in Java. It includes a basic game engine, player classes, and comprehensive JUnit tests for the main game logic.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Internet connection (for downloading JUnit dependencies)

## Project Structure

```
blackjack/
  Action.java
  BlackJackGame.java
  BlackJackGameTest.java
  GamePhase.java
  deck/
    Card.java
    Deck.java
    Rank.java
    Suit.java
  player/
    DealerPlayer.java
    Hand.java
    Player.java
    RealPlayer.java
```

## Running the Tests

```bash
# From repository root
./gradlew :blackjack:test
```

## Test Cases

The project includes several test cases that verify different aspects of the game:

1. `testDealerWins`
   - Tests a scenario where the dealer wins against a player
   - Verifies correct balance updates when player loses

2. `testTie`
   - Tests a scenario where the player and dealer tie
   - Verifies that the player's bet is returned

3. `testPlayerStandsAndWins`
   - Tests a scenario where the player wins by standing
   - Verifies correct balance updates when player wins

4. `testPlayerHitsAndWins`
   - Tests a scenario where the player wins after hitting
   - Verifies that players can hit and still win
   - Tests dealer busting scenario

5. `testPlayerHitsAndBusts`
   - Tests a scenario where the player busts after hitting
   - Verifies correct balance updates when player busts

6. `testMultiplePlayersAndMultipleHits`
   - Tests a scenario with multiple players
   - Verifies turn order and multiple hits
   - Tests game flow with multiple players against dealer

## Notes
- No build system (like Maven or Gradle) is required.
- The test suite covers various game scenarios including:
  - Basic win/lose scenarios
  - Ties
  - Hitting and standing
  - Busting
  - Multiple players
  - Multiple hits
  - Balance updates
  - Dealer behavior
- To add more tests, create additional test methods in the `BlackJackGameTest` class.

## License
This project is for educational purposes. 
