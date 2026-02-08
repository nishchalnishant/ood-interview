package atm.bank;

import atm.bank.enums.AccountType;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

// Represents a bank account with balance, card details, and PIN security
public class Account {
    // Calculates MD5 hash of the PIN for secure storage
    private static byte[] calculateMd5(String pinNumber) {
        try {
            return MessageDigest.getInstance("MD5").digest(pinNumber.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographicException("MD5 algorithm not available in the current environment", e);
        }
    }
    private BigDecimal balance;
    private final String accountNumber;
    private final String cardNumber;
    private final byte[] cardPinHash;
    private final AccountType accountType;

    // Creates a new account with initial zero balance and hashed PIN
    public Account(final String accountNumber,
                   final AccountType type,
                   final String cardNumber,
                   final String pin) {
        this.accountNumber = accountNumber;
        this.accountType = type;
        this.cardNumber = cardNumber;
        this.cardPinHash = calculateMd5(pin);  // PIN is hashed for security
        this.balance = BigDecimal.ZERO;
    }

    // Validates the entered PIN against stored hash
    public boolean validatePin(String pinNumber) {
        byte[] entryPinHash = calculateMd5(pinNumber);
        return Arrays.equals(cardPinHash, entryPinHash);
    }

    // Updates account balance by adding the specified amount
    public void updateBalanceWithTransaction(final BigDecimal balanceChange) {
        this.balance = this.balance.add(balanceChange);
    }

    // Returns current account balance
    public BigDecimal getBalance() {
        return balance;
    }

    // Returns account number
    public String getAccountNumber() {
        return accountNumber;
    }

    // Returns associated card number
    public String getCardNumber() {
        return cardNumber;
    }

    // Returns account type (e.g., SAVING, CHECKING)
    public AccountType getAccountType() {
        return accountType;
    }


}
