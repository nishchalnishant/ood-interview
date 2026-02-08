package grocery;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    // Map of barcodes to their stock quantities
    private final Map<String, Integer> stock = new HashMap<>();

    // Adds or updates stock quantity for an item
    public void addStock(String barcode, int count) {
        stock.put(barcode, stock.getOrDefault(barcode, 0) + count);
    }

    // Reduces stock quantity for an item
    public void reduceStock(String barcode, int count) {
        stock.put(barcode, stock.getOrDefault(barcode, 0) - count);
    }

    // Gets the current stock quantity for an item
    public int getStock(String barcode) {
        return stock.getOrDefault(barcode, 0);
    }
}
