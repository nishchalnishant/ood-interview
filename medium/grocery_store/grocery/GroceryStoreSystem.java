package grocery;

import grocery.discount.DiscountCampaign;

import java.util.ArrayList;
import java.util.List;

public class GroceryStoreSystem {
    // Product catalog containing all available items
    private final Catalog catalog;
    // Inventory tracking system
    private final Inventory inventory;
    // List of active discount campaigns
    private List<DiscountCampaign> activeDiscounts = new ArrayList<>();
    // Checkout system for processing orders
    private final Checkout checkout;

    // Creates a new grocery store system with default components
    public GroceryStoreSystem(){
        this.catalog = new Catalog();
        this.inventory = new Inventory();
        this.checkout = new Checkout(activeDiscounts);
    }

    // Creates a new grocery store system with custom components
    public GroceryStoreSystem(Catalog catalog, Inventory inventory, List<DiscountCampaign> activeDiscounts) {
        this.catalog = catalog;
        this.inventory = inventory;
        this.activeDiscounts = activeDiscounts;
        this.checkout = new Checkout(activeDiscounts);
    }

    // Adds or updates an item in the catalog
    public void addOrUpdateItem(Item item) {
        catalog.updateItem(item);
    }

    // Updates the inventory count for an item
    public void updateInventory(String barcode, int count) {
        inventory.addStock(barcode, count);
    }

    // Adds a new discount campaign to the system
    public void addDiscountCampaign(DiscountCampaign discount) {
        activeDiscounts.add(discount);
    }

    // Returns the checkout system
    public Checkout getCheckout() {
        return checkout;
    }

    // Retrieves an item from the catalog by its barcode
    public Item getItemByBarcode(String barcode) {
        return catalog.getItem(barcode);
    }

    // Removes an item from the catalog
    public void removeItem(String barcode) {
        catalog.removeItem(barcode);
    }
}



