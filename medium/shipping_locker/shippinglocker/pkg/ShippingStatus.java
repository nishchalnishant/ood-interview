package shippinglocker.pkg;

public enum ShippingStatus {
    CREATED,        // Initial state when package is first created
    PENDING,        // Package is ready for locker assignment
    IN_LOCKER,      // Package has been assigned to a locker
    RETRIEVED,      // Package has been picked up by the customer
    EXPIRED,        // Package has exceeded maximum storage time
}
