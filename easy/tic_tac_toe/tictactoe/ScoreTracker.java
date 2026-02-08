package tictactoe;

import java.util.*;
import java.util.stream.Collectors;

class ScoreTracker {
    // Stores player ratings in a map where key is player and value is their score
    private HashMap<Player, Integer> playerRatings = new HashMap<>();

    // Updates player ratings based on game outcome
    // Winner gets +1 point, loser gets -1 point, no change for draw
    // This logic is customizable and, in reality, will use a complex ranking algorithm. For the interview, we use a simple victory count system where the winner gets one point, the loser loses a point, and no changes occur for a draw.
    public void reportGameResult(Player player1, Player player2, Optional<Player> winningPlayer) {
        if (winningPlayer.isPresent()) 
        {
            Player winner = winningPlayer.get();
            Player loser = player1 == winner ? player2 : player1;
            playerRatings.putIfAbsent(winner, 0);
            playerRatings.put(winner, playerRatings.get(winner) + 1);
            playerRatings.putIfAbsent(loser, 0);
            playerRatings.put(loser, playerRatings.get(loser) - 1);
        }
    }

    // Returns a list of players sorted by their ratings in descending order
    public List<Player> getTopPlayers() {
        return playerRatings.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Returns the rank of a player (1-based) based on their rating
    public int getRank(Player player) 
    {
        List<Player> sortedPlayers = playerRatings.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return sortedPlayers.indexOf(player) + 1;
    }

    // Returns the complete map of player ratings
    public HashMap<Player, Integer> getPlayerRatings() 
    {
        return playerRatings;
    }
}
