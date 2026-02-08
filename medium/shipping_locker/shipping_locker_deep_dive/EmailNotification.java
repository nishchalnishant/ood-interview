package shippinglocker;

import shippinglocker.account.Account;
import shippinglocker.locker.LockerEventObserver;

// Implementation of LockerEventObserver that sends email notifications
class EmailNotification implements LockerEventObserver {
    // Updates the observer with a message and sends it to the account owner
    public void update(String message, Account account) {
        // Email sent to account owner
    }
}
