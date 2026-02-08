package restaurant;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ToStringBuilder;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.reservation.Reservation;
import restaurant.table.Layout;
import restaurant.table.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {
    /*
    * Test the restaurant reservation system by creating a reservation and checking if the reservation is correct.
    */
    @Test
    public void testRestaurantReservation() {
        System.out.println("\n=== Testing Restaurant Reservation System ===");
        
        System.out.println("\n--- Setting Up Restaurant Menu ---");
        Menu menu = new Menu();
        menu.addItem(
                new MenuItem("Burger", "a burger", BigDecimal.valueOf(10.0), MenuItem.Category.MAIN));
        System.out.println("✓ Added menu item: Burger ($10.00) - Main course");

        System.out.println("\n--- Creating Restaurant Layout ---");
        Layout layout = new Layout(List.of(4, 4, 6, 6, 10));
        System.out.println("✓ Created table layout with 5 tables:");
        System.out.println("  - Table 1: 4 seats");
        System.out.println("  - Table 2: 4 seats");
        System.out.println("  - Table 3: 6 seats");
        System.out.println("  - Table 4: 6 seats");
        System.out.println("  - Table 5: 10 seats");

        System.out.println("\n--- Initializing Restaurant ---");
        Restaurant testRestaurant = new Restaurant("BurgerShack", menu, layout);
        System.out.println("✓ Restaurant 'BurgerShack' initialized with menu and layout");

        System.out.println("\n--- Finding Available Time Slots ---");
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1).truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        LocalDateTime[] possibleReservation =
                testRestaurant.findAvailableTimeSlots(reservationTime, reservationTime.plusHours(4), 6);
        System.out.println("✓ Searching for available 6-seat tables between " + 
                reservationTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + 
                " and " + reservationTime.plusHours(4).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertEquals(5, possibleReservation.length);
        System.out.println("✓ Found " + possibleReservation.length + " available time slots");
        
        LocalDateTime selectedTime = possibleReservation[2];
        assertEquals(selectedTime, reservationTime.plusHours(2));
        System.out.println("✓ Selected reservation time: " + selectedTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        System.out.println("\n--- Creating Scheduled Reservation ---");
        Reservation reservation = testRestaurant.createScheduledReservation("TestParty", 6, selectedTime);
        System.out.println("✓ Created reservation for 'TestParty' (6 people) at " + selectedTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertEquals("TestParty", reservation.getPartyName());
        assertEquals(6, reservation.getPartySize());
        assertEquals(selectedTime, reservation.getTime());
        assertEquals(6, reservation.getAssignedTable().getCapacity());
        System.out.println("✓ Reservation details verified:");
        System.out.println("  - Party name: " + reservation.getPartyName());
        System.out.println("  - Party size: " + reservation.getPartySize() + " people");
        System.out.println("  - Reserved time: " + reservation.getTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("  - Assigned table capacity: " + reservation.getAssignedTable().getCapacity() + " seats");

        System.out.println("\n--- Verifying Table Booking ---");
        // check if the table is actually booked
        assertFalse(reservation.getAssignedTable().isAvailableAt(selectedTime));
        assertTrue(testRestaurant.getReservationManager().getReservations().contains(reservation));
        System.out.println("✓ Table booking verification passed:");
        System.out.println("  - Table is no longer available at reserved time");
        System.out.println("  - Reservation is properly stored in reservation manager");
        System.out.println("=== Restaurant Reservation Test Completed Successfully ===\n");
    }

    /*
    * Test the restaurant walk-in system by creating a walk-in and checking if the walk-in can be accepted.
     */
    @Test
    public void testRestaurantWalkIn() {
        System.out.println("\n=== Testing Restaurant Walk-In System ===");
        
        System.out.println("\n--- Setting Up Restaurant Menu ---");
        Menu menu = new Menu();
        menu.addItem(
                new MenuItem("Burger", "a burger", BigDecimal.valueOf(10.0), MenuItem.Category.MAIN));
        System.out.println("✓ Added menu item: Burger ($10.00) - Main course");

        System.out.println("\n--- Creating Restaurant Layout ---");
        Layout layout = new Layout(List.of(4, 4, 6, 6, 10));
        System.out.println("✓ Created table layout with 5 tables (4, 4, 6, 6, 10 seats)");

        System.out.println("\n--- Initializing Restaurant ---");
        Restaurant testRestaurant = new Restaurant("BurgerShack", menu, layout);
        System.out.println("✓ Restaurant 'BurgerShack' initialized");

        System.out.println("\n--- Processing Walk-In Reservation ---");
        Reservation walkIn = testRestaurant.createWalkInReservation("WalkIn", 4);
        System.out.println("✓ Created walk-in reservation for 'WalkIn' party (4 people)");
        Table table = walkIn.getAssignedTable();
        assertEquals(4, table.getCapacity());
        assertEquals(4, table.getCurrentPartySize());
        System.out.println("✓ Walk-in assignment verified:");
        System.out.println("  - Assigned table capacity: " + table.getCapacity() + " seats");
        System.out.println("  - Current party size: " + table.getCurrentPartySize() + " people");

        System.out.println("\n--- Processing Order ---");
        table.addOrder(menu.getItem("Burger"), 4);
        System.out.println("✓ Added order: 4 Burgers ($10.00 each)");
        assertEquals(BigDecimal.valueOf(40.0), table.calculateBillAmount());
        System.out.println("✓ Bill calculation verified: $" + table.calculateBillAmount());
        System.out.println("  - 4 Burgers × $10.00 = $40.00");
        System.out.println("=== Restaurant Walk-In Test Completed Successfully ===\n");
    }
}
