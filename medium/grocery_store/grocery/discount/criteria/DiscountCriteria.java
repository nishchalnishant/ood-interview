package grocery.discount.criteria;

import grocery.Item;

// Interface for discount criteria that determine if a discount applies to an item
public interface DiscountCriteria {
    // Checks if the discount criteria applies to the given item
    boolean isApplicable(Item item);
}

