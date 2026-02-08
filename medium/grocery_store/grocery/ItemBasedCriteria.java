package grocery;


import grocery.discount.criteria.DiscountCriteria;

public class ItemBasedCriteria implements DiscountCriteria {
    private final String itemId;

    public ItemBasedCriteria(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean isApplicable(Item item) {
        return item.getBarcode().equals(itemId);
    }
}
