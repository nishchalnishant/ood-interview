# Tic-Tac-Toe - LLD Interview Guide

## Problem Statement

Design a Tic-Tac-Toe game that supports two players playing on a 3x3 grid. The system should handle turn management, validate moves, detect winners, track scores across multiple games, and handle edge cases like invalid moves.

## Clarifying Questions to Ask

1. **Game Rules**: Is this standard 3x3 Tic-Tac-Toe or should it support variable board sizes (e.g., 4x4, 5x5)?
2. **Players**: Will it always be two players, or should we support single-player vs AI?
3. **Score Tracking**: Do we need to track statistics across multiple games?
4. **Multiplayer**: Is this a local game or should it support network multiplayer?
5. **UI**: Do we need to implement the UI or just the game logic?
6. **Extensions**: Should we consider future extensions like undo/redo, game history, or different game modes?

## Core Requirements

### Functional Requirements
- Two players can play the game alternately
- Players Mark 'X' and 'O' on a 3x3 grid
- System validates moves (position not taken, correct turn, game not ended)
- System detects win conditions (3 in a row/column/diagonal)
- System detects draw (board full, no winner)
- Track scores across multiple games
- Support starting new games

### Non-Functional Requirements
- **Performance**: Move validation and winner detection should be O(1) or O(N) where N=3
- **Maintainability**: Code should be easily extensible for different board sizes
- **Reliability**: Invalid moves should throw clear exceptions

### Out of Scope
- Network multiplayer
- AI opponent
- Undo/redo functionality
- Game replay/history
- Graphical UI implementation

## Object-Oriented Design

### Key Classes and Responsibilities

| Class | Responsibility |
|-------|---------------|
| `Game` | Orchestrates the game flow, manages turns, validates moves, determines game status |
| `Board` | Maintains the 3x3 grid state, detects winners, checks if board is full |
| `Player` | Represents a player with name and symbol (X or O) |
| `ScoreTracker` | Tracks wins, losses, and draws across multiple games |
| `Move` | Represents a single move with position coordinates |
| `GameCondition` | Enum representing game states (IN_PROGRESS, ENDED) |

### Design Patterns Used

1. **Single Responsibility Principle**: Each class has one clear responsibility
   - `Board` handles grid state
   - `Game` handles game orchestration
   - `ScoreTracker` handles statistics

2. **Encapsulation**: Internal state of `Board` (the grid) is private and modified only through controlled methods

3. **Validation Pattern**: `Game` class centralizes all move validation logic

### Class Diagram (Key Relationships)

```
Game "1" *-- "1" Board
Game "1" *-- "1" ScoreTracker  
Game "1" o-- "2" Player
Board "1" o-- "0..9" Player (on grid)
```

## Implementation Details

### Core Interfaces and Classes

#### Player Class
```java
package tictactoe;

public class Player {
    private final String name;
    private final String symbol; // 'X' or 'O'
    
    public Player(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
    
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
}
```

#### Board Class - Winner Detection Logic
The winner detection algorithm checks:
- **Rows**: All 3 positions in each row
- **Columns**: All 3 positions in each column  
- **Main Diagonal**: Top-left to bottom-right
- **Anti-Diagonal**: Top-right to bottom-left

**Time Complexity**: O(N) where N=3, effectively O(1) for fixed size

```java
public Optional<Player> getWinner() {
    // Check rows
    for (int i = 0; i < grid.length; i++) {
        Player first = grid[i][0];
        if (first != null && Arrays.stream(grid[i]).allMatch(p -> p == first)) {
            return Optional.of(first);
        }
    }
    // Similar logic for columns and diagonals...
}
```

#### Game Class - Move Validation
```java
public void makeMove(int colIndex, int rowIndex, Player player) {
    // 1. Validate game hasn't ended
    if (getGameStatus().equals(GameCondition.ENDED)) {
        throw new IllegalStateException("game ended");
    }
    // 2. Validate correct player's turn
    if (players[currentPlayerIndex] != player) {
        throw new IllegalArgumentException("not the current player");
    }
    // 3. Validate position not taken
    if(board.getPlayerAt(colIndex, rowIndex) != null) {
        throw new IllegalArgumentException("board position is taken");
    }
    // Update board and switch turns
    board.updateBoard(colIndex, rowIndex, player);
    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
}
```

## Common Interview Questions

### Design Questions

**Q1: How would you extend this to support an N×N board?**
- Make grid size a constructor parameter: `Player[][] grid = new Player[size][size]`
- Winner detection remains the same algorithm, just parameterized by size
- Time complexity becomes O(N)

**Q2: How would you add an AI player?**
- Create an abstract `PlayerStrategy` interface with `getNextMove()` method
- Implement `HumanPlayerStrategy` and `AIPlayerStrategy`
- AI could use Minimax algorithm for optimal play
- `Player` class would have a `PlayerStrategy` field

**Q3: How do you handle concurrent moves in a networked game?**
- Add synchronization to `makeMove()` method
- Use optimistic locking with version numbers on the board state
- Implement event-driven architecture where moves are queued and processed sequentially
- Add move timestamps for conflict resolution

**Q4: How would you implement undo/redo?**
- **Command Pattern**: Create a `MoveCommand` with `execute()` and `undo()` methods
- Maintain a stack of executed commands for undo
- Maintain a stack of undone commands for redo
- Store previous board state in each command

### Extension Questions

**Q1: How would you add support for different win conditions?**
- Extract winner detection into a `WinConditionStrategy` interface
- Implement different strategies: `ThreeInRowStrategy`, `FourCornersStrategy`, etc.
- Inject strategy into `Board` class

**Q2: How would you add a time limit per turn?**
- Add `Timer` class that starts when turn begins
- Use scheduled executor to trigger timeout events
- Add `onTimeout()` handler that forfeits the turn or game

**Q3: How would you support saving/loading games?**
- Implement `Serializable` interface or use JSON serialization
- Create `GameState` class to capture: board state, current player, scores
- Implement `save()` and `load()` methods using file I/O
- Store in database for persistent storage

### Trade-offs Discussion

**1. Using 2D Array vs HashMap for Board**
- **Array (Current)**: 
  - ✅ Faster access O(1), better cache locality
  - ✅ Simple and straightforward for fixed-size grid
  - ❌ Cannot easily resize
- **HashMap**: 
  - ✅ Flexible for sparse boards or variable sizes
  - ❌ More memory overhead, slower access

**2. Eager vs Lazy Winner Detection**
- **Eager (Current)**: Check winner after each move
  - ✅ Simpler logic, immediate feedback
  - ❌ Repeated checks (though O(1) for 3x3)
- **Lazy**: Only check when `getWinner()` is called
  - ✅ Potentially fewer checks
  - ❌ More complex state management

**3. Immutable vs Mutable Board**
- **Mutable (Current)**:
  - ✅ Memory efficient
  - ❌ Harder to implement undo/redo
- **Immutable**:
  - ✅ Easier to implement undo, safer for concurrent access
  - ❌ More memory usage (new board per move)

## Code Walkthrough

### Typical Game Flow
```java
// 1. Create players
Player playerX = new Player("Alice", "X");
Player playerO = new Player("Bob", "O");

// 2. Initialize game
Game game = new Game(playerX, playerO);

// 3. Players make moves alternately
game.makeMove(0, 0, playerX);  // X at (0,0)
game.makeMove(1, 1, playerO);  // O at (1,1)
game.makeMove(0, 1, playerX);  // X at (0,1)
game.makeMove(1, 0, playerO);  // O at (1,0)
game.makeMove(0, 2, playerX);  // X wins! (row 0)

// 4. Check game status
GameCondition status = game.getGameStatus(); // ENDED

// 5. View scores
ScoreTracker scores = game.getScoreTracker();
scores.getWinCount(playerX); // 1

// 6. Start new game
game.startNewGame(playerX, playerO);
```

## Testing Strategy

### Unit Tests
- **Board Tests**: Winner detection for all scenarios (rows, columns, diagonals, no winner)
- **Game Tests**: Turn validation, move validation, game status transitions
- **ScoreTracker Tests**: Correct score updates for wins/losses/draws

### Integration Tests
- Complete game scenarios (X wins, O wins, draw)
- Multiple games with score accumulation
- Edge cases: filling entire board with draw

### Edge Cases to Test
1. Attempting to move after game ends
2. Wrong player attempting to move out of turn
3. Moving to already occupied position
4. Detecting winner in all 8 possible winning lines (3 rows, 3 columns, 2 diagonals)
5. Detecting draw when board is full

## Common Pitfalls to Avoid

1. **Not validating moves**: Always check game state, turn order, and position availability
2. **Forgetting diagonal checks**: Winner detection must check both diagonals
3. **Off-by-one errors**: Array indexing for 3x3 grid (0-2, not 1-3)
4. **Not handling null**: Grid positions start as null, must check before comparison
5. **Tight coupling**: Board logic should be independent of Game orchestration
6. **Magic numbers**: Use constants for board size instead of hardcoded `3`

## Time/Space Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Make Move | O(N) - winner check | O(1) |
| Check Winner | O(N) - check all lines | O(1) |
| Check Board Full | O(N²) - check all cells | O(1) |
| Initialize Game | O(N²) - clear board | O(N²) - store grid |

Where N = board size (3 for standard Tic-Tac-Toe)

## Real-World Considerations

### Concurrency
- For networked game, protect `makeMove()` with locks or use message queue
- Use atomic operations for turn management
- Consider event-driven architecture for move notifications

### Scalability
- For tournament system, separate `Game` from `Match` and `Tournament` classes
- Store completed games in database, not in-memory
- Use stateless game  service with database persistence

### Extensibility
- **Strategy Pattern** for different board sizes, win conditions, player types
- **Observer Pattern** for UI updates and event notifications
- **Factory Pattern** for creating different game variants

### Database Design (if needed)
```sql
CREATE TABLE games (
    game_id INT PRIMARY KEY,
    player1_id INT,
    player2_id INT,
    winner_id INT NULL,
    board_state JSON,
    created_at TIMESTAMP
);
```

## Follow-up Discussion Points

1. **Optimization**: How would you optimize winner detection for very large boards (e.g., 100×100)?
   - Use heuristic checking only around last move
   - Maintain running counts for each row/column/diagonal
   
2. **Variants**: How would you implement Connect-4 or Gomoku (5-in-a-row)?
   - Vertical board instead of horizontal
   - Gravity simulation for pieces falling
   - Different win condition (4 or 5 in a row)

3. **Mobile Considerations**: What changes for mobile app?
   - Add touch gesture handling
   - Persist game state for app backgrounding
   - Optimize for different screen sizes

4. **Machine Learning**: How would you add move suggestions?
   - Train neural network on historical games
   - Integrate with `AIPlayerStrategy`
   - Show probability heatmap on board

---

**Interview Preparation Tips:**
- Start by clarifying requirements (board size, player types, etc.)
- Draw the class diagram while explaining
- Explain design decisions and trade-offs
- Discuss how you'd test each component
- Be ready to code the core `makeMove()` and `getWinner()` methods
- Practice explaining the winner detection algorithm clearly
