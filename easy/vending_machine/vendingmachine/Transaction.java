package vendingmachine;

import java.math.BigDecimal;

public class Transaction {
    private Rack rack;
    private Product product;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Getters and setters
    public void setProduct(Product product) {
        this.product = product;
    }
    public Product getProduct() {
        return product;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }
    public Rack getRack() {
        return rack;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "totalAmount=" + totalAmount +
                ", rack=" + rack +
                ", product=" + product +
                ", totalAmount=" + getTotalAmount() +
                '}';
    }
}
