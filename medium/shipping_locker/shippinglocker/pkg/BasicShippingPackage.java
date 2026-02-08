package shippinglocker.pkg;

import shippinglocker.account.Account;
import shippinglocker.locker.LockerSize;
import java.math.BigDecimal;

// Basic implementation of a shipping package
public class BasicShippingPackage implements ShippingPackage {
    // Unique identifier for the order
    private final String orderId;
    // User account associated with this package
    private final Account user;
    // Width of the package in inches
    private final BigDecimal width;
    // Height of the package in inches
    private final BigDecimal height;
    // Depth of the package in inches
    private final BigDecimal depth;
    // Current status of the package
    private ShippingStatus status;

    // Creates a new shipping package with specified dimensions
    public BasicShippingPackage(String orderId, Account user, BigDecimal width, BigDecimal height, BigDecimal depth) {
        this.orderId = orderId;
        this.user = user;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.status = ShippingStatus.CREATED;
    }

    // Returns the order identifier
    @Override
    public String getOrderId() {
        return orderId;
    }

    // Returns the user account
    @Override
    public Account getUser() {
        return user;
    }

    // Returns the package width
    @Override
    public BigDecimal getWidth() {
        return width;
    }

    // Returns the package height
    @Override
    public BigDecimal getHeight() {
        return height;
    }

    // Returns the package depth
    @Override
    public BigDecimal getDepth() {
        return depth;
    }

    // Returns the current package status
    @Override
    public ShippingStatus getStatus() {
        return status;
    }

    // Updates the package status
    @Override
    public void updateShippingStatus(ShippingStatus status) {
        this.status = status;
    }

    // Determines the smallest locker size that can fit this package
    @Override
    public LockerSize getLockerSize() {
        for(LockerSize size : LockerSize.values()) {
            if(size.getWidth().compareTo(width) >= 0 && 
               size.getHeight().compareTo(height) >= 0 && 
               size.getDepth().compareTo(depth) >= 0) {
                return size;
            }
        }
        throw new PackageIncompatibleException("No locker size available for the package");
    }
}
