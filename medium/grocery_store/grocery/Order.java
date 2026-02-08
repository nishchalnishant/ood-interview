package grocery;

import grocery.discount.DiscountCampaign;

import java.math.BigDecimal;
import java.util.*;

public class Order {
    // Unique identifier for the order
    private final String orderId;
    // List of items in the order
    private final List<OrderItem> items = new ArrayList<>();
    // Map of items to their applied discounts
    private final Map<OrderItem, DiscountCampaign> appliedDiscounts = new HashMap<>();
    // Amount paid by the customer
    private BigDecimal paymentAmount = BigDecimal.ZERO;

    // Creates a new order with a random UUID
    public Order() {
        this.orderId = String.valueOf(UUID.randomUUID());
    }

    // Adds an item to the order
    public void addItem(OrderItem item) {
        items.add(item);
    }

    // Calculates the subtotal of all items without discounts
    public BigDecimal calculateSubtotal() {
        return items.stream()
                .map(OrderItem::calculatePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Calculates the total price including all applied discounts
    public BigDecimal calculateTotal() {
        return items.stream()
                .map(item -> {
                    DiscountCampaign discount = appliedDiscounts.get(item);
                    return discount != null ? item.calculatePriceWithDiscount(discount) : item.calculatePrice();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Applies a discount to a specific item in the order
    public void applyDiscount(OrderItem item, DiscountCampaign discount) {
        appliedDiscounts.put(item, discount);
    }

    // Returns an unmodifiable list of items in the order
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    // Sets the payment amount for the order
    public void setPayment(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    // Returns the payment amount
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    // Calculates the change to be returned to the customer
    public BigDecimal calculateChange() {
        return paymentAmount.subtract(calculateTotal());
    }

    // Returns the map of applied discounts
    public Map<OrderItem, DiscountCampaign> getAppliedDiscounts() {
        return appliedDiscounts;
    }

    // Returns the order ID
    public String getOrderId() {
        return orderId;
    }
}
