package atm.hardware.input;

import atm.ATMMachine;
import atm.bank.enums.TransactionType;

import java.math.BigDecimal;

// Interface for keypad hardware component
public interface Keypad {
    // Handles PIN entry from user
    void handlePinEntry(String pin, ATMMachine machine);

    // Handles amount entry from user
    void handleAmountEntry(BigDecimal amount, ATMMachine machine);

    // Handles transaction type selection from user
    void handleSelectTransaction(TransactionType transactionType, ATMMachine atmMachine);
}
