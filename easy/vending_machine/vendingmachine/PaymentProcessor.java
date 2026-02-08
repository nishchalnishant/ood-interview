package vendingmachine;

import java.math.BigDecimal;

public class PaymentProcessor {
    private BigDecimal currentBalance = BigDecimal.ZERO;
    public void addBalance(BigDecimal amount){
        currentBalance = currentBalance.add(amount);
    }
    public void charge(BigDecimal amount){
        currentBalance = currentBalance.subtract(amount);
    }
    public BigDecimal returnChange() {
        BigDecimal change = currentBalance;
        currentBalance = BigDecimal.ZERO;
        return change;
    }

    // Getter and setters
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
}