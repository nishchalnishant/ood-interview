package shippinglocker;

import shippinglocker.account.Account;

public interface NotificationInterface {
    void sendNotification(String message, Account user);
}
