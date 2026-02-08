package grocery;

import java.util.HashMap;
import java.util.Map;

public class Catalog {
    // Map of barcodes to their corresponding items
    private final Map<String, Item> items = new HashMap<>();

    // Updates or adds an item to the catalog
    public void updateItem(Item item) {
        items.put(item.getBarcode(), item);
    }

    // Removes an item from the catalog by its barcode
    public void removeItem(String barcode) {
        items.remove(barcode);
    }

    // Retrieves an item from the catalog by its barcode
    public Item getItem(String barcode) {
        return items.get(barcode);
    }
}
