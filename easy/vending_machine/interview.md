# Vending Machine - LLD Interview Guide

## Problem Statement

Design a vending machine system that allows users to select products, insert money, dispense products, return change, and manage inventory. The system should handle transactions, validate payments, track inventory, and maintain transaction history.

## Clarifying Questions to Ask

1. **Product Management**: How many types of products? Fixed or dynamic inventory?
2. **Payment**: What payment methods? (coins, bills, cards, digital payments?)
3. **Change**: Does the machine need to dispense change? Manage coin/bill inventory?
4. **Inventory**: Should we track stock levels? Alert on low inventory?
5. **Cancellation**: Can users cancel transactions? When and how?
6. **Concurrent Users**: Can multiple people use it simultaneously? (typically no for physical machine)
7. **Admin Functions**: Do we need restocking, price updates, transaction reports?

## Core Requirements

### Functional Requirements
- Display available products with prices
- Accept money (coins, bills)
- Allow product selection
- Validate sufficient payment and product availability
- Dispense selected product
- Return change to customer
- Cancel transaction and refund money
- Track inventory levels
- Maintain transaction history
- Support restocking and price updates

### Non-Functional Requirements
- **Reliability**: Handle concurrent access safely (if applicable)
- **Accuracy**: Precise money calculations (no rounding errors)
- **Usability**: Clear error messages for invalid operations
- **Maintainability**: Easy to add new product types

### Out of Scope
- Card payment integration
- Remote monitoring/IoT features
- Multiple vending machines management
- User accounts/loyalty programs

## Object-Oriented Design

### Key Classes and Responsibilities

| Class | Responsibility |
|-------|---------------|
| `VendingMachine` | Main orchestrator; coordinates transactions and manages components |
| `Product` | Represents an item (name, price, ID) |
| `Rack` | Holds products of one type; tracks quantity |
| `InventoryManager` | Manages all racks and product stock levels |
| `PaymentProcessor` | Handles money insertion, charging, and returning change |
| `Transaction` | Records details of a single purchase (product, amount, timestamp) |
| `InvalidTransactionException` | Custom exception for transaction errors |

### Design Patterns Used

1. **Facade Pattern**: `VendingMachine` provides simplified interface, hiding complexity of inventory, payment, etc.
2. **Single Responsibility**: Each class has focused responsibility
   - `InventoryManager` → inventory only
   - `PaymentProcessor` →  payment only
   - `Transaction` → record keeping only
3. **Encapsulation**: Internal balances and inventory hidden; controlled via methods
4. **Exception Handling**: Custom exceptions for domain-specific errors

### Class Relationships
```
VendingMachine "1" *-- "1" InventoryManager
VendingMachine "1" *-- "1" PaymentProcessor
VendingMachine "1" *-- "*" Transaction (history)
InventoryManager "1" o-- "*" Rack
Rack "1" o-- "1" Product
Transaction "1" o-- "1" Product
```

## Implementation Details

### Transaction Flow

```java
// 1. Insert money
vendingMachine.insertMoney(new BigDecimal("5.00"));

// 2. Choose product
vendingMachine.chooseProduct("A1"); // Rack ID

// 3. Confirm transaction
Transaction tx = vendingMachine.confirmTransaction();
// - Validates: product exists, sufficient funds, stock available
// - Charges payment
// - Dispenses product
// - Returns change
// - Records transaction

// OR cancel if changed mind
vendingMachine.cancelTransaction(); // Refunds money
```

### Key Validation Logic

```java
private void validateTransaction() throws InvalidTransactionException {
    if (currentTransaction.getProduct() == null) {
        throw new InvalidTransactionException("Invalid product selection");
    }
    if (currentTransaction.getRack().getProductCount() == 0) {
        throw new InvalidTransactionException("Insufficient inventory");
    }
    if (paymentProcessor.getCurrentBalance().compareTo(
            currentTransaction.getProduct().getUnitPrice()) < 0) {
        throw new InvalidTransactionException("Insufficient funds");
    }
}
```

### Money Handling with BigDecimal

**Critical**: Always use `BigDecimal` for money, never `float` or `double`!

```java
class PaymentProcessor {
    private BigDecimal currentBalance = BigDecimal.ZERO;
    
    void addBalance(BigDecimal amount) {
        currentBalance = currentBalance.add(amount);
    }
    
    void charge(BigDecimal price) {
        currentBalance = currentBalance.subtract(price);
    }
    
    BigDecimal returnChange() {
        BigDecimal change = currentBalance;
        currentBalance = BigDecimal.ZERO;
        return change;
    }
}
```

## Common Interview Questions

### Design Questions

**Q1: How would you handle giving exact change when the machine runs out of coins?**
- Maintain `CoinInventory` with counts of each denomination ($1, $0.25, $0.10, $0.05, $0.01)
- Implement **greedy algorithm** to dispense optimal coin combination
- If cannot make exact change, either:
  - Reject transaction and return all money
  - Dispense product but log "change owed" for compensation
  - Display "Exact change required" status

**Q2: How would you add card payment support?**
- Create `PaymentMethod` interface with `authorize()` and `capture()` methods
- Implement `CashPayment` and `CardPayment` classes
- Integrate with payment gateway API (Stripe, Square)
- Handle payment states: authorized, captured, refunded
- Add timeout for card transactions

**Q3: How do you handle someone trying to select multiple products in one transaction?**
- **Option 1** (Simple): Only allow one product per transaction (current design)
- **Option 2** (Shopping Cart): 
  - Add `ShoppingCart` class to hold multiple selected items
  - Calculate total before payment
  - Dispense all items atomically or rollback

**Q4: How would you implement a "Buy 2, Get 1 Free" promotion?**
- Create `PromotionEngine` with `PricingRule` interface
- Implement rules: `QuantityDiscount`, `BuyXGetYFree`, `PercentageOff`
- Apply rules during `confirmTransaction()` before charging
- Store promotion metadata with `Transaction` for reporting

### Extension Questions

**Q1: How would you add product expiration date tracking?**
- Add `expirationDate` field to `Product`
- Implement FIFO dispensing from `Rack`
- Scheduled task to check and alert on near-expiration items
- Admin function to remove expired products

**Q2: How would you support dynamic pricing (time-based, temperature-based)?**
- Extract pricing logic into `PricingStrategy` interface
- Implement: `StaticPricing`, `TimeBasedPricing`, `DemandBasedPricing`
- Inject strategy into `Product` or `InventoryManager`
- Recalculate price at transaction time, not display time

**Q3: How would you add a touch screen UI with product images?**
- Create `DisplayController` class to manage UI state
- Implement Observer pattern for UI updates (inventory changes, balance updates)
- Separate business logic (current design) from presentation logic
- Use MVC or MVVM architecture

### Trade-offs Discussion

**1. Transaction State Management**
- **Stateful (Current)**: `currentTransaction` persisted in VendingMachine
  - ✅ Simple, tracks user actions across steps
  - ❌ Not thread-safe, assumes single user
- **Stateless**: Pass `Transaction` as parameter to each method
  - ✅ Thread-safe, supports concurrent users
  - ❌ More complex API, user manages state

**2. Inventory Data Structure**
- **Map<String, Rack> (Current)**: Keyed by rack ID (e.g., "A1", "B3")
  - ✅ Fast lookup by position, matches physical layout
  - ❌ Harder to search by product type
- **Map<ProductType, List<Rack>>**: Multiple racks per product
  - ✅ Easier to find all locations of same product
  - ❌ Slower lookup by position

**3. Change Dispensing Strategy**
- **Greedy Algorithm**: Always use largest denomination first
  - ✅ Minimizes coins dispensed, fast O(c) where c=coin types
  - ❌ May fail when optimal solution exists (e.g., 30 cents with only quarters and pennies)
- **Dynamic Programming**: Find exact change combination
  - ✅ Always finds solution if exists
  - ❌ Slower O(n*amount), overkill for small amounts

## Code Walkthrough

### Complete Transaction Example
```java
VendingMachine vm = new VendingMachine();

// Admin: Set up inventory
Map<String, Rack> racks = new HashMap<>();
Rack rackA1 = new Rack();
rackA1.addProduct(new Product("Coke", new BigDecimal("1.50"), "COKE"));
rackA1.setProductCount(10);
racks.put("A1", rackA1);
vm.setRack(racks);

// Customer: Purchase flow
vm.insertMoney(new BigDecimal("2.00"));  // Insert $2
vm.chooseProduct("A1");                   // Select Coke

try {
    Transaction tx = vm.confirmTransaction();
    // Success: Product dispensed, change = $0.50
    System.out.println("Change: " + tx.getTotalAmount());
} catch (InvalidTransactionException e) {
    System.out.println("Transaction failed: " + e.getMessage());
    vm.cancelTransaction(); // Refund money
}
```

## Testing Strategy

### Unit Tests
- **PaymentProcessor**: Test add balance, charge, return change scenarios
- **InventoryManager**: Test rack updates, product dispensing, stock levels
- **VendingMachine**: Mock dependencies, test transaction validations

### Integration Tests
- Complete transaction flows (success, out of stock, insufficient funds)
- Transaction history recording
- Cancellation and refunds

### Edge Cases
1. Selecting product without inserting money
2. Inserting money without selecting product
3. Exact payment (no change needed)
4. Overpayment (change required)
5. Last item in stock
6. Selecting empty rack
7. Float precision with BigDecimal (e.g., $0.10 + $0.20 = $0.30 exactly)

## Common Pitfalls to Avoid

1. **Using float/double for money**: Always use `BigDecimal` to avoid rounding errors
2. **Not validating transaction before dispensing**: Check all conditions first
3. **Forgetting to reset state**: Clear `currentTransaction` after completion/cancellation
4. **Not handling sold-out products**: Check inventory before allowing selection
5. **Thread safety**: Protect shared state if supporting concurrent access
6. **Not returning change**: Always refund excess payment
7. **Ignoring transaction history**: Important for reconciliation and auditing

## Time/Space Complexity

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Insert Money | O(1) | O(1) |
| Choose Product | O(1) - HashMap lookup | O(1) |
| Confirm Transaction | O(1) | O(1) per transaction |
| Get Transaction History | O(n) - return all | O(n) - store all |
| Restock | O(1) per rack | O(1) |

## Real-World Considerations

### Concurrency
- Add `synchronized` to transaction methods or use `ReentrantLock`
- Or use state machine with atomic state transitions
- For distributed systems, use optimistic locking with version numbers

### Persistence
- Store inventory and transaction history in database
- Use transactional writes for purchase operations (ACID)
```sql
CREATE TABLE transactions (
    id INT PRIMARY KEY,
    product_id INT,
    price DECIMAL(10,2),
    payment_amount DECIMAL(10,2),
    change_amount DECIMAL(10,2),
    timestamp TIMESTAMP
);

CREATE TABLE inventory (
    rack_id VARCHAR(10) PRIMARY KEY,
    product_id INT,
    quantity INT,
    CHECK (quantity >= 0)
);
```

### Monitoring & Alerts
- Low inventory alerts (< 5 items)
- Out-of-stock notifications
- Low change inventory
- Transaction failure rate monitoring
- Revenue tracking

### Hardware Integration
- Interface with physical dispenser motors
- Coin/bill validator integration
- Touch screen or keypad input
- LED display output
- Receipt printer (optional)

### Security
- Audit trail for all transactions (non-repudiation)
- Tamper detection sensors
- Secure storage of cash
- Admin authentication for restocking and reports

## Follow-up Discussion Points

1. **Optimizations**: How would you optimize for high-traffic locations?
   - Pre-compute popular product locations
   - Cache inventory status
   - Batch transaction logging

2. **API Design**: How would you expose this as a REST API for mobile app?
   ```
   POST /api/v1/machines/{id}/transactions
   POST /api/v1/machines/{id}/transactions/{txid}/insert-money
   POST /api/v1/machines/{id}/transactions/{txid}/select-product
   POST /api/v1/machines/{id}/transactions/{txid}/confirm
   DELETE /api/v1/machines/{id}/transactions/{txid}
   ```

3. **Analytics**: What metrics would you track?
   - Top selling products
   - Average transaction value
   - Peak usage times
   - Stockout frequency per product
   - Change dispensing failures

4. **Multi-machine Management**: How would you manage a fleet of 100+ machines?
   - Central inventory management system
   - Real-time sync of stock levels
   - Route optimization for restocking trucks
   - A/B testing of product placements

---

**Interview Preparation Tips:**
- Always clarify payment methods and change handling requirements
- Discuss money precision (BigDecimal) without being prompted
- Explain transaction atomicity (all-or-nothing)
- Be ready to code `confirmTransaction()` with full validation
- Discuss real-world constraints (physical inventory, coin supply)
- Consider edge cases: power failure during transaction, coin jam, etc.
