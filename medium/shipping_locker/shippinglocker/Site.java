package shippinglocker;

import shippinglocker.locker.Locker;
import shippinglocker.locker.LockerSize;
import shippinglocker.pkg.ShippingPackage;
import shippinglocker.pkg.ShippingStatus;
import shippinglocker.pkg.NoLockerAvailableException;

import java.util.*;

// Represents a physical site containing multiple lockers of different sizes
public class Site {
    // Map of locker sizes to sets of lockers of that size
    final Map<LockerSize, Set<Locker>> lockers = new HashMap<>();

    // Creates a new site with specified number of lockers for each size
    public Site(Map<LockerSize, Integer> lockers) {
        for (Map.Entry<LockerSize, Integer> entry : lockers.entrySet()) {
            Set<Locker> lockerSet = new HashSet<>();
            for (int i = 0; i < entry.getValue(); i++) {
                lockerSet.add(new Locker(entry.getKey()));
            }
            this.lockers.put(entry.getKey(), lockerSet);
        }
    }

    // Finds an available locker of the specified size
    public Locker findAvailableLocker(LockerSize size) {
        for (Locker locker : lockers.get(size)) {
            if (locker.isAvailable()) {
                return locker;
            }
        }
        return null;
    }

    // Places a package in an available locker of appropriate size
    public Locker placePackage(ShippingPackage pkg, Date date) {
        // Determine the smallest locker size that can fit this package
        LockerSize size = pkg.getLockerSize();
        Locker locker = findAvailableLocker(size);
        if (locker != null) {
            locker.assignPackage(pkg, date);
            pkg.updateShippingStatus(ShippingStatus.IN_LOCKER);
            return locker;
        } 
        throw new NoLockerAvailableException("No locker of size " + size + " is currently available");
    }
}
