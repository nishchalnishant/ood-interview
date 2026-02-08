package grocery.discount.strategy;

import java.math.BigDecimal;

// Decorator that adds a fixed amount discount to an existing strategy
public class FixedDiscountDecorator implements DiscountCalculationStrategy {
    // The strategy being decorated
    private final DiscountCalculationStrategy strategy;
    // The fixed amount to be added to the discount
    private final BigDecimal fixedAmount;

    // Creates a new fixed discount decorator with the specified strategy and fixed amount
    public FixedDiscountDecorator(DiscountCalculationStrategy strategy, BigDecimal fixedAmount) {
        this.strategy = strategy;
        this.fixedAmount = fixedAmount;
    }

    // Calculates the discounted price by applying both the base strategy and the fixed amount
    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        return strategy.calculateDiscountedPrice(originalPrice).subtract(fixedAmount);
    }
}
