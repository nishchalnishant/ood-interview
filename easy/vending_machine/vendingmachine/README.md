# Vending Machine Project

This project implements a simple vending machine system with the following features:
- Product management
- Inventory tracking
- Transaction processing
- Payment handling

## Running the Tests

```bash
# From repository root
./gradlew :vendingmachine:test
```

### Expected Output

The test will run and you should see:
- Test execution results
- Transaction processing output
- Inventory state
- Success/failure status of the tests

## Project Structure

The project contains the following main classes:
- `VendingMachine`: Main class that orchestrates the vending machine operations
- `Product`: Represents a product in the vending machine
- `Rack`: Manages product inventory in a specific slot
- `Transaction`: Handles individual purchase transactions
- `InventoryManager`: Manages the overall inventory
- `PaymentProcessor`: Handles payment processing
- `InvalidTransactionException`: Custom exception for invalid transactions

## Test Coverage

The `VendingMachineTest` class includes an end-to-end test that verifies:
- Product selection
- Payment processing
- Inventory management
- Transaction history
- Error handling