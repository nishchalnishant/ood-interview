package restaurant.table;

import java.time.LocalDateTime;
import java.util.*;

// Manages the collection of tables in the restaurant and their arrangement
public class Layout {
    private final Map<Integer, Table> tablesById = new HashMap<>();
    // Groups tables by their capacity for efficient table assignment, sorted from smallest to largest capacity
    private final SortedMap<Integer, Set<Table>> tablesByCapacity = new TreeMap<>();

    public Layout(List<Integer> tableCapacities) {
        for (int i = 0; i < tableCapacities.size(); i++) {
            int capacity = tableCapacities.get(i);
            Table table = new Table(i, capacity);
            tablesById.put(i, table);
            tablesByCapacity.computeIfAbsent(capacity, k -> new HashSet<>()).add(table);
        }
    }

    // Finds the smallest available table that can accommodate a party of the given size at the given time
    public Table findAvailableTable(int partySize, LocalDateTime reservationTime) {
        for (Set<Table> tables : tablesByCapacity.tailMap(partySize).values()) {
            for (Table table : tables) {
                if (table.isAvailableAt(reservationTime)) {
                    return table;
                }
            }
        }
        return null;
    }
}
