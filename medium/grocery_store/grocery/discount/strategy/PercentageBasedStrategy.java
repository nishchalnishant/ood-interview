package grocery.discount.strategy;

import java.math.BigDecimal;

// Strategy that applies a percentage-based discount
public class PercentageBasedStrategy implements DiscountCalculationStrategy {
    // The percentage to be discounted (e.g., 50 for 50%)
    private final BigDecimal percentage;

    // Creates a new percentage-based strategy with the specified percentage
    public PercentageBasedStrategy(BigDecimal percentage) {
        this.percentage = percentage;
    }

    // Calculates the discounted price by applying the percentage discount
    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        return originalPrice.multiply(BigDecimal.ONE.subtract(percentage.divide(BigDecimal.valueOf(100))));
    }
}
