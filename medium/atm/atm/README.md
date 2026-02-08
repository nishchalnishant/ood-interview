# ATM System

A Java-based ATM (Automated Teller Machine) system that simulates basic banking operations like deposits, withdrawals, and PIN validation.

## Project Structure

```
atm/
├── ATMMachine.java         # Main ATM machine class
├── ATMTest.java            # Test suite for ATM functionality
├── bank/                   # Banking system components
│   ├── Account.java
│   ├── Bank.java
│   ├── BankInterface.java
│   ├── Transaction.java
│   └── enums/
│       ├── AccountType.java
│       └── TransactionType.java
├── hardware/              # Hardware component interfaces and simulations
│   ├── input/
│   │   ├── CardProcessor.java
│   │   ├── DepositBox.java
│   │   ├── Keypad.java
│   │   └── simulated implementations
│   └── output/
│       ├── CashDispenser.java
│       ├── Display.java
│       └── simulated implementations
└── states/               # State pattern implementation
    ├── ATMState.java
    ├── IdleState.java
    ├── PinEntryState.java
    └── other state classes
```

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- JUnit 5 (Jupiter)

## Running the Tests

```bash
# From repository root
./gradlew :atm:test
```

## Test Cases

The test suite includes the following test cases:

1. `testEndToEndDeposit()`: Tests a complete deposit transaction
2. `testMultipleDeposits()`: Tests multiple deposit transactions
3. `testMultipleWithdrawals()`: Tests multiple withdrawal transactions
4. `testInvalidCardInsertion()`: Tests handling of invalid card insertion
5. `testInvalidPinEntry()`: Tests handling of invalid PIN entry
6. `testWithdrawWithoutSufficientFunds()`: Tests withdrawal with insufficient funds
7. `testEjectCardDuringTransaction()`: Tests card ejection during a transaction

## Notes

- The project uses simulated hardware components for testing
- All monetary values are handled using BigDecimal for precision
- PINs are stored as MD5 hashes for security
- The system supports both checking and savings accounts 
