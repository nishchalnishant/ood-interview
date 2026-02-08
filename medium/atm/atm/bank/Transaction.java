package atm.bank;

import atm.bank.enums.TransactionType;

// Base interface for all types of bank transactions with common operations
public interface Transaction {
    TransactionType getType();

    boolean validateTransaction();

    void executeTransaction();
}
