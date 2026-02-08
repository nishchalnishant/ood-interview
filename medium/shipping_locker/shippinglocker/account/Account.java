package shippinglocker.account;

import java.math.BigDecimal;

// Represents a user account in the locker system
public class Account {
    // Unique identifier for the account
    private final String accountId;
    // Name of the account owner
    private final String ownerName;
    // Policy defining locker usage rules for this account
    private final AccountLockerPolicy lockerPolicy;
    // Total charges accumulated for locker usage
    private BigDecimal usageCharges = new BigDecimal("0.00");

    // Creates a new account with specified details and policy
    public Account(String accountId, String ownerName, AccountLockerPolicy lockerPolicy) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.lockerPolicy = lockerPolicy;
    }

    // Returns the name of the account owner
    public String getOwnerName() {
        return ownerName;
    }

    // Returns the locker policy associated with this account
    public AccountLockerPolicy getLockerPolicy() {
        return lockerPolicy;
    }

    // Returns the unique identifier of this account
    public String getAccountId() {
        return accountId;
    }

    // Adds a charge to the account's total usage charges
    public void addUsageCharge(BigDecimal amount) {
        usageCharges = usageCharges.add(amount);
    }

    // Returns the total usage charges for this account
    public BigDecimal getUsageCharges() {
        return usageCharges;
    }
}
