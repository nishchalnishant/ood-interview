package shippinglocker.locker;

import shippinglocker.account.Account;

// Interface for objects that need to be notified of locker events
public interface LockerEventObserver {
    // Updates the observer with a message and the affected account
    void update(String message, Account account);
}
