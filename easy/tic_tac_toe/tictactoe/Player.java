package tictactoe;

public class Player {
    // Player's name identifier
    private final String name;
    // Player's symbol (X or O)
    private final char symbol;
    
    // Creates a new player with a name and symbol
    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    // Returns the player's name
    public String getName() {
        return name;
    }

    // Returns a string representation of the player (their name)
    @Override
    public String toString(){
        return name;
    }

    // Returns the player's symbol (X or O)
    public char getSymbol() {
        return symbol;
    }
}
