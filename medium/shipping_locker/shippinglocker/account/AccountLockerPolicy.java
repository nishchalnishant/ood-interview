package shippinglocker.account;

// Defines the policy for locker usage for an account
public class AccountLockerPolicy {
    // Number of days of free storage
    final int freePeriodDays;
    // Maximum number of days a package can be stored
    final int maximumPeriodDays;

    // Creates a new locker policy with specified free and maximum periods
    public AccountLockerPolicy(int freePeriodDays, int maximumPeriodDays) {
        this.freePeriodDays = freePeriodDays;
        this.maximumPeriodDays = maximumPeriodDays;
    }

    // Returns the number of free storage days
    public int getFreePeriodDays() {
        return freePeriodDays;
    }

    // Returns the maximum allowed storage period in days
    public int getMaximumPeriodDays() {
        return maximumPeriodDays;
    }
}
