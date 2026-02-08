# ATM System - LLD Interview Guide

## Problem Statement

Design an ATM (Automated Teller Machine) system that allows customers to perform banking operations like checking balance, withdrawing cash, depositing money, and changing PIN after authentication.

## Clarifying Questions

1. **Operations**: Which operations? (withdraw, deposit, balance check, PIN change, mini-statement?)
2. **Authentication**: Card + PIN? Biometric? Two-factor?
3. **Banking**: Single bank or multi-bank support?
4. **Cash Management**: Do we track ATM cash inventory? Denominations?
5. **Concurrent Users**: Single user at a time (typical) or queue management?
6. **Network**: Online (live bank verification) or offline capable?

## Core Requirements

### Functional Requirements
- Card insertion and validation
- PIN entry and verification (max 3 attempts)
- Account balance inquiry
- Cash withdrawal (with sufficient balance and ATM cash)
- Cash/check deposit
- PIN change
- Transaction receipt printing
- Card ejection

### Non-Functional Requirements
- **Security**: Encrypted PIN storage, secure communication
- **Reliability**: Handle network failures gracefully
- **Usability**: Clear error messages, timeout handling
- **Auditability**: Log all transactions

## Key Classes

| Class | Responsibility |
|-------|---------------|
| `ATMMachine` | Main orchestrator, manages states and hardware |
| `Bank/BankInterface` | External bank system integration |
| `Account` | Customer account (balance, PIN) |
| `Transaction` | Record of operations (type, amount, timestamp) |
| `CardProcessor` | Reads card data |
| `CashDispenser` | Dispenses cash, tracks inventory |
| `DepositBox` | Accepts deposits |
| `Keypad` | Captures user input |
| `Display` | Shows messages to user |
| `ATMState` | Interface for state pattern |
| `IdleState`, `PinEntryState`, `TransactionState` | Concrete states |

## Design Patterns

1. **State Pattern**: Different ATM states (Idle, CardInserted, PinEntry, Transaction, etc.)
2. **Adapter Pattern**: `BankInterface` to adapt to different bank systems
3. **Factory Pattern**: Creating different types of transactions
4. **Facade Pattern**: `ATMMachine` provides simple interface to complex subsystems

### State Machine
```
Idle → CardInserted → PinEntry → Authenticated → Transaction → Idle
                 ↓         ↓             ↓
              EjectCard  EjectCard    EjectCard
```

## Common Interview Questions

**Q1: How do you handle security (PIN storage, transmission)?**
- Store PIN as **salted hash** (MD5/SHA-256), never plaintext
- Encrypt network communication (TLS/SSL)
- Timeout after 30-60 seconds of inactivity
- Lock card after 3 failed PIN attempts
- Physically secure cash dispenser

**Q2: What happens if network fails during withdrawal?**
- **Two-phase commit**: 
  1. Reserve funds from bank (debit account)
  2. Dispense cash
  3. Confirm transaction
- If network fails after step 1: Reverse transaction later (compensating transaction)
- If cash dispensing fails: Reverse bank debit immediately
- Log all partial transactions for reconciliation

**Q3: How do you manage ATM cash inventory?**
```java
class CashDispenser {
    Map<Denomination, Integer> inventory; // $100: 50 notes, $50: 100 notes, etc.
    
    boolean canDispense(BigDecimal amount) {
        // Greedy algorithm to check if amount can be formed
    }
    
    Map<Denomination, Integer> dispense(BigDecimal amount) {
        // Return combination of notes
        // Update inventory
        // Alert if low on cash
    }
}
```

**Q4: How would you implement daily withdrawal limits?**
- Store `dailyLimit` and `usedToday` in `Account`
- Reset `usedToday` at midnight (scheduled job)
- Check `usedToday + withdrawalAmount <= dailyLimit` before allowing transaction
- Or query bank for limit enforcement (online)

## Implementation Highlights

### PIN Validation with Retry Limit
```java
class PinEntryState implements ATMState {
    private int attemptCount = 0;
    private static final int MAX_ATTEMPTS = 3;
    
    void enterPin(String pin) {
        if (bank.validatePin(card, pin)) {
            atm.transitionTo(new AuthenticatedState());
        } else {
            attemptCount++;
            if (attemptCount >= MAX_ATTEMPTS) {
                atm.lockCard(card);
                atm.ejectCard();
            }
        }
    }
}
```

### Withdrawal Transaction Flow
```java
void withdraw(BigDecimal amount) {
    // 1. Validate amount (positive, multiples of $20)
    // 2. Check account balance
    if (account.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundsException();
    }
    // 3. Check ATM cash availability
    if (!cashDispenser.canDispense(amount)) {
        throw new ATMOutOfCashException();
    }
    // 4. Debit account
    bank.debit(account, amount);
    // 5. Dispense cash
    cashDispenser.dispense(amount);
    // 6. Log transaction
    transactionHistory.add(new Transaction(WITHDRAW, amount));
    // 7. Update display
    display.show("Please take your cash");
}
```

## Testing Strategy

- Test happy path (successful withdrawal)
- Test invalid PIN (3 attempts, card lock)
- Test insufficient balance
- Test ATM out of cash
- Test network failure scenarios
- Test concurrent access (if applicable)
- Test state transitions

## Common Pitfalls

1. Not using BigDecimal for money
2. Storing PIN in plaintext
3. Not handling partial transactions (network failures)
4. Not limiting PIN attempts
5. Not tracking ATM cash inventory
6. Not implementing timeout (security)
7. Forgetting to eject card after error

## Real-World Considerations

- **Hardware integration**: Interface with physical card reader, PIN pad, cash dispenser
- **Compliance**: PCI DSS for card security, banking regulations
- **Monitoring**: Real-time alerts for low cash, failed transactions, tampering
- **Maintenance**: Cash replenishment, receipt paper, software updates
- **Multi-currency**: Support for  different currencies
- **Accessibility**: Support for visually impaired (audio), multiple languages

---

**Key Topics:**
- State pattern for ATM flow
- Security (PIN hashing, encryption)
- Transaction atomicity and rollback
- Cash dispensing algorithm
- Network failure handling
