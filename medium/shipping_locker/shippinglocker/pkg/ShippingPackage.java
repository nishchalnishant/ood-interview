package shippinglocker.pkg;

import shippinglocker.account.Account;
import shippinglocker.locker.LockerSize;
import java.math.BigDecimal;

// Interface defining the contract for shipping packages
public interface ShippingPackage {
    // Returns the unique order identifier
    String getOrderId();

    // Returns the user account associated with this package
    Account getUser();

    // Returns the width of the package
    BigDecimal getWidth();

    // Returns the height of the package
    BigDecimal getHeight();

    // Returns the depth of the package
    BigDecimal getDepth();

    // Returns the current status of the package
    ShippingStatus getStatus();

    // Updates the status of the package
    void updateShippingStatus(ShippingStatus status);

    // Determines the appropriate locker size for this package
    LockerSize getLockerSize();
}
