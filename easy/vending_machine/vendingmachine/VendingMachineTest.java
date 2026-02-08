package vendingmachine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.*;

public class VendingMachineTest {
    @Test
    public void testEndToEndVending() throws InvalidTransactionException {
        System.out.println("\n=== Testing Vending Machine System: End-to-End Operations ===");
        
        System.out.println("\n--- Initializing Vending Machine ---");
        final VendingMachine machine = new VendingMachine();
        System.out.println("✓ Vending machine initialized");

        System.out.println("\n--- Setting Up Products and Inventory ---");
        final Product itemA = new Product("a", "Product A", new BigDecimal("1.0"));
        final Product itemB = new Product("b", "Product B", new BigDecimal("1.5"));
        final Product itemC = new Product("c", "Product C", new BigDecimal("1.25"));
        machine.setRack(
                Map.of("A1", new Rack("A1", itemA, 10),
                        "A2", new Rack("A2", itemB, 5),
                        "A3", new Rack("A3", itemC, 15)));
        System.out.println("✓ Products loaded into vending machine:");
        System.out.println("  - A1: Product A ($1.00) - 10 units available");
        System.out.println("  - A2: Product B ($1.50) - 5 units available");
        System.out.println("  - A3: Product C ($1.25) - 15 units available");

        System.out.println("\n--- First Purchase Transaction ---");
        machine.insertMoney(new BigDecimal("20.00"));
        System.out.println("✓ Inserted $20.00 into machine");
        machine.chooseProduct("A2");
        System.out.println("✓ Selected Product B (A2) - Price: $1.50");
        Transaction transaction = machine.confirmTransaction();
        assertEquals(itemB, transaction.getProduct());
        assertEquals(new BigDecimal("18.50"), transaction.getTotalAmount());
        System.out.println("✓ Transaction completed successfully:");
        System.out.println("  - Product dispensed: Product B");
        System.out.println("  - Change returned: $18.50");

        System.out.println("\n--- Second Purchase Transaction ---");
        machine.insertMoney(new BigDecimal("6.00"));
        System.out.println("✓ Inserted $6.00 into machine");
        machine.chooseProduct("A2");
        System.out.println("✓ Selected Product B (A2) - Price: $1.50");
        transaction = machine.confirmTransaction();
        assertEquals(itemB, transaction.getProduct());
        assertEquals(new BigDecimal("4.50"), transaction.getTotalAmount());
        System.out.println("✓ Transaction completed successfully:");
        System.out.println("  - Product dispensed: Product B");
        System.out.println("  - Change returned: $4.50");

        System.out.println("\n--- Third Purchase Attempt (Should Fail) ---");
        machine.insertMoney(new BigDecimal("1.00"));
        System.out.println("✓ Inserted $1.00 into machine");
        machine.chooseProduct("A2");
        System.out.println("✓ Selected Product B (A2) - Price: $1.50");
        System.out.println("✓ Attempting to purchase with insufficient funds...");
        InvalidTransactionException exception = assertThrows(InvalidTransactionException.class, () -> {
            machine.confirmTransaction();
        });
        assertEquals("Insufficient fund", exception.getMessage());
        System.out.println("✓ Transaction correctly rejected:");
        System.out.println("  - Error: Insufficient fund");
        System.out.println("  - Expected behavior: Cannot purchase $1.50 item with $1.00");

        System.out.println("\n--- Canceling Failed Transaction ---");
        machine.cancelTransaction();
        System.out.println("✓ Transaction canceled, money returned to customer");

        System.out.println("\n--- Verifying Transaction History ---");
        List<Transaction> history = machine.getTransactionHistory();
        assertEquals(2, history.size());
        assertEquals(itemB, history.get(0).getProduct());
        assertEquals(itemB, history.get(1).getProduct());
        System.out.println("✓ Transaction history verified:");
        System.out.println("  - Total successful transactions: " + history.size());
        System.out.println("  - Both transactions were for Product B");

        System.out.println("\n--- Verifying Inventory Levels ---");
        InventoryManager inventory = machine.getInventoryManager();
        assertEquals(3, inventory.getRack("A2").getProductCount());
        System.out.println("✓ Inventory levels verified:");
        System.out.println("  - Product B (A2) remaining: " + inventory.getRack("A2").getProductCount() + " units");
        System.out.println("  - Started with 5 units, sold 2 units, remaining 3 units");
        System.out.println("=== Vending Machine Test Completed Successfully ===\n");
    }
}
