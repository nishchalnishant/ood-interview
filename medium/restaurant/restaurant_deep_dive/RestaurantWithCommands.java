package restaurant;

import restaurant.table.Layout;
import restaurant.table.Table;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.reservation.Reservation;
import restaurant.reservation.ReservationManager;
import restaurant.reservation.OrderItem;
import restaurant.command.OrderCommand;
import restaurant.command.SendToKitchenCommand;
import restaurant.command.DeliverCommand;
import restaurant.command.CancelCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Enhanced Restaurant class that uses the Command Pattern for order management.
 * This implementation provides better encapsulation and flexibility for handling orders.
 */
public class RestaurantWithCommands {
    private final String name;
    private final Menu menu;
    private final Layout layout;
    private final ReservationManager reservationManager;
    private final OrderManager orderManager;

    /**
     * Creates a new restaurant with the given name, menu, and table layout.
     *
     * @param name  The name of the restaurant
     * @param menu  The menu of the restaurant
     * @param layout The table layout of the restaurant
     */
    public RestaurantWithCommands(String name, Menu menu, Layout layout) {
        this.name = name;
        this.menu = menu;
        this.layout = layout;
        this.reservationManager = new ReservationManager(layout);
        this.orderManager = new OrderManager();
    }

    /**
     * Finds available time slots for a reservation within the given time range.
     *
     * @param rangeStart The start of the time range
     * @param rangeEnd   The end of the time range
     * @param partySize  The size of the party
     * @return An array of available time slots
     */
    public LocalDateTime[] findAvailableTimeSlots(LocalDateTime rangeStart, LocalDateTime rangeEnd, int partySize) {
        return reservationManager.findAvailableTimeSlots(rangeStart, rangeEnd, partySize);
    }

    /**
     * Creates a scheduled reservation for a party.
     *
     * @param partyName The name of the party
     * @param partySize The size of the party
     * @param time      The desired reservation time
     * @return The created reservation
     */
    public Reservation createScheduledReservation(String partyName, int partySize, LocalDateTime time) {
        return reservationManager.createReservation(partyName, partySize, time);
    }

    /**
     * Removes an existing reservation.
     *
     * @param partyName       The name of the party
     * @param partySize       The size of the party
     * @param reservationTime The time of the reservation
     */
    public void removeReservation(String partyName, int partySize, LocalDateTime reservationTime) {
        reservationManager.removeReservation(partyName, partySize, reservationTime);
    }

    /**
     * Creates a walk-in reservation for a party.
     *
     * @param partyName The name of the party
     * @param partySize The size of the party
     * @return The created reservation
     */
    public Reservation createWalkInReservation(String partyName, int partySize) {
        return reservationManager.createReservation(partyName, partySize, LocalDateTime.now());
    }

    /**
     * Places an order for an item at a table and sends it to the kitchen.
     * Uses the Command Pattern to handle the order process.
     *
     * @param table The table placing the order
     * @param item  The menu item being ordered
     */
    public void orderItem(Table table, MenuItem item) {
        table.addOrder(item);
        // Get the last added order item
        List<OrderItem> orderItems = table.getOrderedItems().get(item);
        if (orderItems != null && !orderItems.isEmpty()) {
            OrderItem lastOrder = orderItems.get(orderItems.size() - 1);
            OrderCommand sendToKitchen = new SendToKitchenCommand(lastOrder);
            orderManager.addCommand(sendToKitchen);
            orderManager.executeCommands();
        }
    }

    /**
     * Cancels an order for an item at a table.
     * Uses the Command Pattern to handle the cancellation process.
     *
     * @param table The table canceling the order
     * @param item  The menu item being canceled
     */
    public void cancelItem(Table table, MenuItem item) {
        List<OrderItem> orderItems = table.getOrderedItems().get(item);
        if (orderItems != null && !orderItems.isEmpty()) {
            OrderItem lastOrder = orderItems.get(orderItems.size() - 1);
            OrderCommand cancelOrder = new CancelCommand(lastOrder);
            orderManager.addCommand(cancelOrder);
            orderManager.executeCommands();
            table.removeOrder(item);
        }
    }

    /**
     * Delivers an item to the customer at a table.
     * Uses the Command Pattern to handle the delivery process.
     *
     * @param table The table receiving the delivery
     * @param item  The menu item being delivered
     */
    public void deliverItem(Table table, MenuItem item) {
        List<OrderItem> orderItems = table.getOrderedItems().get(item);
        if (orderItems != null && !orderItems.isEmpty()) {
            OrderItem lastOrder = orderItems.get(orderItems.size() - 1);
            OrderCommand deliverOrder = new DeliverCommand(lastOrder);
            orderManager.addCommand(deliverOrder);
            orderManager.executeCommands();
        }
    }

    /**
     * Calculates the total bill amount for a table.
     *
     * @param table The table to calculate the bill for
     * @return The total bill amount
     */
    public BigDecimal calculateTableBill(Table table) {
        return table.calculateBillAmount();
    }

    // Getters
    public String getName() {
        return name;
    }

    public Menu getMenu() {
        return menu;
    }

    public Layout getLayout() {
        return layout;
    }

    public ReservationManager getReservationManager() {
        return reservationManager;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }
} 
