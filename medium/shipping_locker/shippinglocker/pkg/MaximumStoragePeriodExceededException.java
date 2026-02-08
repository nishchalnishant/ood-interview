package shippinglocker.pkg;

// Exception thrown when a package exceeds its maximum allowed storage period
public class MaximumStoragePeriodExceededException extends RuntimeException {
    // Creates a new exception with the specified message
    public MaximumStoragePeriodExceededException(String message) {
        super(message);
    }
} 
