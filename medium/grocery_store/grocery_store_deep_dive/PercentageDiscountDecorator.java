package grocery.discount.strategy;

import java.math.BigDecimal;

// Decorator that adds a percentage discount to an existing strategy
public class PercentageDiscountDecorator implements DiscountCalculationStrategy {
    // The strategy being decorated
    private final DiscountCalculationStrategy strategy;
    // The additional percentage to be discounted
    private final BigDecimal additionalPercentage;

    // Creates a new percentage discount decorator with the specified strategy and additional percentage
    public PercentageDiscountDecorator(DiscountCalculationStrategy strategy, BigDecimal additionalPercentage) {
        this.strategy = strategy;
        this.additionalPercentage = additionalPercentage;
    }

    // Calculates the discounted price by applying both the base strategy and the additional percentage
    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        BigDecimal baseDiscountedPrice = strategy.calculateDiscountedPrice(originalPrice);
        return baseDiscountedPrice.multiply(BigDecimal.ONE.subtract(additionalPercentage.divide(BigDecimal.valueOf(100))));
    }
}
