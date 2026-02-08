package grocery.discount.criteria;

import grocery.Item;

// Criteria that applies discounts based on item category
public class CategoryBasedCriteria implements DiscountCriteria {
    // The category to check against
    private final String category;

    // Creates a new category-based criteria with the specified category
    public CategoryBasedCriteria(String category) {
        this.category = category;
    }

    // Checks if the item belongs to the specified category
    @Override
    public boolean isApplicable(Item item) {
        return item.getCategory().equals(category);
    }
}
