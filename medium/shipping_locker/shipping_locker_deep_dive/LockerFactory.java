package shippinglocker.locker;

// Factory class for creating Locker instances
class LockerFactory {
    // Creates a new locker of the specified size
    public static Locker createLocker(LockerSize size) {
        return switch (size) {
            case SMALL -> new Locker(LockerSize.SMALL);
            case MEDIUM -> new Locker(LockerSize.MEDIUM);
            case LARGE -> new Locker(LockerSize.LARGE);
        };
    }
}
