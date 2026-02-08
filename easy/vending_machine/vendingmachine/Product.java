package vendingmachine;

import java.math.BigDecimal;

public class Product {
    private final String productCode;
    private final String description;
    private final BigDecimal unitPrice;

    public Product(String productCode, String description, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.description = description;
        this.unitPrice = unitPrice;
    }


    // Getter and setters
    public String getProductCode() {
        return productCode;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}