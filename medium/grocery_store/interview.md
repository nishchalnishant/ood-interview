# Grocery Store/Point of Sale System - LLD Interview Guide

## Problem Statement

Design a grocery store checkout system (POS - Point of Sale) handling product scanning, pricing, discounts, and payment processing.

## Key Requirements

- Product catalog with barcodes
- Shopping cart management
- Pricing (unit price, weighted items)
- Discounts/promotions (BOGO, percentage off)
- Multiple payment methods
- Receipt generation

## Key Classes

- `POSSystem`: Main checkout system
- `Product`: Item details (barcode, name, price)
- `ShoppingCart`: Current transaction items
- `PricingEngine`: Calculate total with discounts
- `Payment`: Handle cash/card/digital payments
- `Receipt`: Transaction record

## Common Questions

**Q1: How do you handle different pricing models?**
```java
interface PricingStrategy {
    BigDecimal calculatePrice(Product product, int quantity);
}

class UnitPricing implements PricingStrategy {
    BigDecimal calculatePrice(Product product, int quantity) {
        return product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }
}

class WeightPricing implements PricingStrategy {
    BigDecimal calculatePrice(Product product, double weight) {
        return product.getPricePerKg().multiply(BigDecimal.valueOf(weight));
    }
}
```

**Q2: How do you apply discounts/promotions?**
```java
interface Promotion {
    BigDecimal apply(ShoppingCart cart);
}

class BuyOneGetOneFree implements Promotion {
    private String productId;
    
    BigDecimal apply(ShoppingCart cart) {
        int quantity = cart.getQuantity(productId);
        int freeItems = quantity / 2;
        return product.getPrice().multiply(BigDecimal.valueOf(freeItems));
    }
}
```

**Q3: How do you handle inventory management?**
- Decrement stock when item scanned
- Alert on low inventory
- Handle returns (increment stock)

## Key Topics
- Strategy pattern for pricing
- Promotion engine design
- Payment processing
- Inventory synchronization
- Barcode scanning integration
