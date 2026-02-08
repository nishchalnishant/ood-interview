package grocery;

import java.math.BigDecimal;

public class Item {
    // Name of the item
    private final String name;
    // Unique barcode identifier
    private final String barcode;
    // Category of the item (e.g., Fruit, Candy)
    private final String category;
    // Price of the item
    private final BigDecimal price;

    // Creates a new item with the specified details
    public Item(String name, String barcode, String category, BigDecimal price) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.price = price;
    }

    // Returns the name of the item
    public String getName() {
        return name;
    }

    // Returns the barcode of the item
    public String getBarcode() {
        return barcode;
    }

    // Returns the category of the item
    public String getCategory() {
        return category;
    }

    // Returns the price of the item
    public BigDecimal getPrice() {
        return price;
    }
}
