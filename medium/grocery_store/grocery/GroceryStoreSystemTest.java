package grocery;

import grocery.discount.DiscountCampaign;
import grocery.discount.criteria.CategoryBasedCriteria;
import grocery.discount.strategy.PercentageBasedStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GroceryStoreSystemTest {

    @Test
    public void testEndToEnd() {
        System.out.println("\n=== Testing Grocery Store System End-to-End ===");
        
        System.out.println("\n--- Initializing Grocery Store System ---");
        // initialize grocery store
        GroceryStoreSystem groceryStoreSystem = new GroceryStoreSystem();
        System.out.println("✓ Grocery store system initialized");

        System.out.println("\n--- Setting Up Product Catalog ---");
        // set up catalog, inventory, and example discount
        groceryStoreSystem.addOrUpdateItem(new Item("Apple", "123", "Fruit", new BigDecimal("0.5")));
        groceryStoreSystem.addOrUpdateItem(new Item("Banana", "124", "Fruit", new BigDecimal("1.0")));
        groceryStoreSystem.addOrUpdateItem(new Item("Gum", "125", "Candy", new BigDecimal("4.0")));
        System.out.println("✓ Added Apple (barcode: 123, category: Fruit, price: $0.50)");
        System.out.println("✓ Added Banana (barcode: 124, category: Fruit, price: $1.00)");
        System.out.println("✓ Added Gum (barcode: 125, category: Candy, price: $4.00)");

        System.out.println("\n--- Setting Up Inventory ---");
        groceryStoreSystem.updateInventory("123", 100);
        groceryStoreSystem.updateInventory("124", 100);
        groceryStoreSystem.updateInventory("125", 100);
        System.out.println("✓ Set inventory: 100 Apples, 100 Bananas, 100 Gum packs");

        System.out.println("\n--- Creating Discount Campaign ---");
        groceryStoreSystem.addDiscountCampaign(
                new DiscountCampaign("1", "Fruit Half Price",
                        new CategoryBasedCriteria("Fruit"),
                        new PercentageBasedStrategy(new BigDecimal("50"))));
        System.out.println("✓ Created discount campaign: 'Fruit Half Price'");
        System.out.println("  - Applies to all items in 'Fruit' category");
        System.out.println("  - 50% discount on all fruit items");

        System.out.println("\n--- Starting Checkout Session ---");
        // start a new order
        Checkout checkOutSession = groceryStoreSystem.getCheckout();
        System.out.println("✓ New checkout session created");

        System.out.println("\n--- Adding Items to Order ---");
        checkOutSession.addItemToOrder(groceryStoreSystem.getItemByBarcode("123"), 20);
        checkOutSession.addItemToOrder(groceryStoreSystem.getItemByBarcode("124"), 10);
        checkOutSession.addItemToOrder(groceryStoreSystem.getItemByBarcode("125"), 5);
        System.out.println("✓ Added 20 Apples to order (original price: $10.00, with 50% discount: $5.00)");
        System.out.println("✓ Added 10 Bananas to order (original price: $10.00, with 50% discount: $5.00)");
        System.out.println("✓ Added 5 Gum packs to order (no discount: $20.00)");

        System.out.println("\n--- Calculating Order Total ---");
        // verify total
        BigDecimal total = checkOutSession.getOrderTotal();
        System.out.println("✓ Order total calculated: $" + total);
        System.out.println("  - Fruit items (with 50% discount): $10.00");
        System.out.println("  - Candy items (no discount): $20.00");
        System.out.println("  - Total: $30.00");
        assertEquals(new BigDecimal("30.0").stripTrailingZeros(), total.stripTrailingZeros());

        System.out.println("\n--- Processing Payment ---");
        // verify change
        BigDecimal change = checkOutSession.processPayment(BigDecimal.valueOf(100));
        System.out.println("✓ Customer paid: $100.00");
        System.out.println("✓ Change calculated: $" + change);
        System.out.println("  - Amount paid: $100.00");
        System.out.println("  - Order total: $30.00");
        System.out.println("  - Change due: $70.00");
        assertEquals(new BigDecimal("70.0").stripTrailingZeros(), change.stripTrailingZeros());

        System.out.println("\n--- Generating Receipt ---");
        Receipt receipt = checkOutSession.getReceipt();
        System.out.println("✓ Receipt generated successfully");
        System.out.println("=== Receipt Details ===");
        System.out.println(receipt.printReceipt());
        System.out.println("=== End-to-End Test Completed Successfully ===\n");
    }

    // Test managing the catalogue
    @Test
    public void testCatalogueManagement() {
        System.out.println("\n=== Testing Catalog Management System ===");
        
        System.out.println("\n--- Initializing Grocery Store System ---");
        GroceryStoreSystem groceryStoreSystem = new GroceryStoreSystem();
        System.out.println("✓ Grocery store system initialized");

        System.out.println("\n--- Adding Items to Catalog ---");
        groceryStoreSystem.addOrUpdateItem(new Item("Apple", "123", "Fruit", new BigDecimal("0.5")));
        groceryStoreSystem.addOrUpdateItem(new Item("Banana", "124", "Fruit", new BigDecimal("1.0")));
        groceryStoreSystem.addOrUpdateItem(new Item("Gum", "125", "Candy", new BigDecimal("4.0")));
        System.out.println("✓ Added Apple (barcode: 123, category: Fruit, price: $0.50)");
        System.out.println("✓ Added Banana (barcode: 124, category: Fruit, price: $1.00)");
        System.out.println("✓ Added Gum (barcode: 125, category: Candy, price: $4.00)");

        System.out.println("\n--- Verifying Item Retrieval by Barcode ---");
        assertEquals("Apple", groceryStoreSystem.getItemByBarcode("123").getName());
        assertEquals("Banana", groceryStoreSystem.getItemByBarcode("124").getName());
        assertEquals("Gum", groceryStoreSystem.getItemByBarcode("125").getName());
        System.out.println("✓ Successfully retrieved Apple by barcode 123");
        System.out.println("✓ Successfully retrieved Banana by barcode 124");
        System.out.println("✓ Successfully retrieved Gum by barcode 125");

        System.out.println("\n--- Testing Item Update (Price and Category Change) ---");
        // test change a barcode item price and category
        groceryStoreSystem.addOrUpdateItem(
                new Item("Bread", "123", "Pantry", new BigDecimal("0.6")));
        System.out.println("✓ Updated item with barcode 123:");
        System.out.println("  - Name changed from 'Apple' to 'Bread'");
        System.out.println("  - Category changed from 'Fruit' to 'Pantry'");
        System.out.println("  - Price changed from $0.50 to $0.60");
        assertEquals(new BigDecimal("0.6"), groceryStoreSystem.getItemByBarcode("123").getPrice());
        assertEquals("Pantry", groceryStoreSystem.getItemByBarcode("123").getCategory());
        System.out.println("✓ Item update verified successfully");

        System.out.println("\n--- Testing Item Removal ---");
        // test remove item
        groceryStoreSystem.removeItem("123");
        System.out.println("✓ Removed item with barcode 123 from catalog");
        assertNull(groceryStoreSystem.getItemByBarcode("123"));
        System.out.println("✓ Item removal verified: barcode 123 no longer exists in catalog");
        System.out.println("=== Catalog Management Test Completed Successfully ===\n");
    }
}
