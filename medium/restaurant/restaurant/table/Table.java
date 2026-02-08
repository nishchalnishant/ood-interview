package restaurant.table;

import restaurant.menu.MenuItem;
import restaurant.reservation.Reservation;
import restaurant.reservation.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Represents a table in the restaurant with its properties and current state
public class Table {
    // immutable properties
    private final int tableId;
    private final int capacity;

    // current state
    private final Map<LocalDateTime, Reservation> reservations = new HashMap<>();
    private final Map<MenuItem, List<OrderItem>> orderedItems = new HashMap<>();

    public Table(int tableId, int capacity) {
        this.tableId = tableId;
        this.capacity = capacity;
    }

    // Calculates the total bill amount for all ordered items at this table
    public BigDecimal calculateBillAmount() {
        return orderedItems.values().stream()
                .flatMap(List::stream)
                .map(OrderItem::getItem)
                .map(MenuItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Adds multiple orders of the same menu item to the table
    public void addOrder(MenuItem item, int quantity) {
        for (int i = 0; i < quantity; i++) {
            addOrder(item);
        }
    }

    // Adds a single menu item to the table's order
    public void addOrder(MenuItem item) {
        List<OrderItem> orderItems = orderedItems.get(item);
        if (orderItems == null) {
            orderItems = new ArrayList<>();
            orderedItems.put(item, orderItems);
            orderItems.add(new OrderItem(item));
        } else {
            orderItems.add(new OrderItem(item));
        }
    }

    // Removes a menu item from the table's order
    public void removeOrder(MenuItem item) {
        List<OrderItem> orderItems = orderedItems.get(item);
        if (orderItems != null) {
            orderItems.remove(0);
            if (orderItems.isEmpty()) {
                orderedItems.remove(item);
            }
        }
    }

    // Checks if the table is available at a specific time
    public boolean isAvailableAt(LocalDateTime reservationTime) {
        return !reservations.containsKey(reservationTime);
    }

    // Adds a reservation to this table
    public void addReservation(Reservation reservation) {
        reservations.put(reservation.getTime(), reservation);
    }

    // Removes a reservation from this table for a specific time
    public void removeReservation(LocalDateTime reservationTime) {
        reservations.remove(reservationTime);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTableId() {
        return tableId;
    }

    // Returns the current number of guests seated at this table
    public int getCurrentPartySize() {
        return reservations.values().stream()
                .mapToInt(Reservation::getPartySize)
                .sum();
    }

    // Returns the map of ordered items
    public Map<MenuItem, List<OrderItem>> getOrderedItems() {
        return orderedItems;
    }
    
    @Override
    public String toString() {
        return "Table #" + getTableId() + " (Capacity: " + getCapacity() + ")";
    }
}
