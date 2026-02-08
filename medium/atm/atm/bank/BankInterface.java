package atm.bank;

import atm.bank.enums.AccountType;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

// Defines the contract for bank operations that must be implemented by any bank implementation
public interface BankInterface {
    void addAccount(String accountNumber, AccountType type, String cardNumber, String pin);
    boolean validateCard(String cardNumber);
    boolean checkPin(String cardNumber, String pinNumber);
    Account getAccountByAccountNumber(String accountNumber);
    Account getAccountByCard(String cardNumber);

    boolean withdrawFunds(Account account, BigDecimal amount);
}
