package shippinglocker.locker;

import shippinglocker.account.Account;
import shippinglocker.pkg.ShippingPackage;

import java.util.ArrayList;
import java.util.List;

class LockerManagerChange {
    // List of observers that will be notified of locker events
    private final List<LockerEventObserver> observers = new ArrayList<>();

    public void addObserver(LockerEventObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LockerEventObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String message, Account account) {
        for (LockerEventObserver observer : observers) {
            observer.update(message, account);
        }
    }

    // Assigns a package to an available locker and notifies observers.
    public void assignPackage(ShippingPackage pkg) {
        Locker locker = assignLockerToPackage(pkg);
        if (locker != null) {
            notifyObservers("Package assigned to locker ", pkg.getUser());
        }
    }

    private Locker assignLockerToPackage(ShippingPackage pkg) {
        return null;
    }
}
