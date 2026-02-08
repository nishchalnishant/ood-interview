package restaurant;

import restaurant.table.Layout;
import restaurant.table.Table;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.reservation.Reservation;
import restaurant.reservation.ReservationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Main restaurant class that manages reservations, orders and tables
public class Restaurant {
    private final String name;
    private final Menu menu;
    private final Layout layout;
    private final ReservationManager reservationManager;

    public Restaurant(String name, Menu menu, Layout layout) {
        this.name = name;
        this.menu = menu;
        this.layout = layout;
        this.reservationManager = new ReservationManager(layout);
    }

    // Finds possible reservation times within a time range for a party of specified size
    public LocalDateTime[] findAvailableTimeSlots(LocalDateTime rangeStart, LocalDateTime rangeEnd, int partySize) {
        return reservationManager.findAvailableTimeSlots(rangeStart, rangeEnd, partySize);
    }

    // Creates a reservation for a party at the specified time
    public Reservation createScheduledReservation(String partyName, int partySize, LocalDateTime time) {
        return reservationManager.createReservation(partyName, partySize, time);
    }

    // Removes an existing reservation
    public void removeReservation(String partyName, int partySize, LocalDateTime reservationTime) {
        reservationManager.removeReservation(partyName, partySize, reservationTime);
    }

    // Creates a reservation for a party without prior reservation
    public Reservation createWalkInReservation(String partyName, int partySize) {
        return reservationManager.createReservation(partyName, partySize, LocalDateTime.now());
    }

    // Adds an item to a table's order
    public void orderItem(Table table, MenuItem item) {
        table.addOrder(item);
    }

    // Removes an item from a table's order
    public void cancelItem(Table table, MenuItem item) {
        table.removeOrder(item);
    }

    // Calculates the bill amount for a table
    public BigDecimal calculateTableBill(Table table) {
        return table.calculateBillAmount();
    }

    public String getName() {
        return name;
    }

    public Menu getMenu() {
        return menu;
    }

    public Layout getLayout() {
        return layout;
    }

    // Returns the restaurant's reservation manager
    public ReservationManager getReservationManager() {
        return reservationManager;
    }
}

