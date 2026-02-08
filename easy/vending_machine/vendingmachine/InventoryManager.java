package vendingmachine;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private Map<String, Rack> racks;

    public InventoryManager() {
        racks = new HashMap<>();
    }

    public Product getProductInRack(String rackCode) {
        return racks.get(rackCode).getProduct();
    }

    public void dispenseProductFromRack(Rack rack) {
        rack.setCount(rack.getProductCount() - 1);
    }

    public void updateRack(Map<String, Rack> racks) {
        this.racks = racks;
    }

    public Rack getRack(String name) {
        return racks.get(name);
    }

    @Override
    public String toString() {
        return "InventoryManager{" +
                "racks=" + racks +
                '}';
    }
}
