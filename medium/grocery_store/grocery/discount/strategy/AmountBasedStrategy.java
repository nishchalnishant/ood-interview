package grocery.discount.strategy;

import java.math.BigDecimal;

// Strategy that applies a fixed amount discount
public class AmountBasedStrategy implements DiscountCalculationStrategy {
    // The fixed amount to be discounted
    private final BigDecimal discountAmount;

    // Creates a new amount-based strategy with the specified discount amount
    public AmountBasedStrategy(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    // Calculates the discounted price by subtracting the fixed amount
    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        return originalPrice.subtract(discountAmount).max(BigDecimal.ZERO);
    }
}
