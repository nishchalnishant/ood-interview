package shippinglocker;

import org.junit.jupiter.api.Test;
import shippinglocker.account.Account;
import shippinglocker.account.AccountLockerPolicy;
import shippinglocker.locker.Locker;
import shippinglocker.locker.LockerManager;
import shippinglocker.locker.LockerSize;
import shippinglocker.pkg.BasicShippingPackage;
import shippinglocker.pkg.ShippingPackage;
import shippinglocker.pkg.ShippingStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;
import static shippinglocker.locker.LockerSize.*;

public class ShippingLockerTest {
    @Test
    public void testLockerManager() {
        System.out.println("\n=== Testing Shipping Locker System ===");
        
        System.out.println("\n--- Initializing Locker Manager ---");
        LockerManager lockerManager = createLockerManager();
        System.out.println("✓ Locker manager initialized with test site and accounts");

        System.out.println("\n--- Retrieving Test Account ---");
        Account testAccount = lockerManager.getAccount("testAccount");
        System.out.println("✓ Retrieved account: 'testAccount' (Owner: " + testAccount.getOwnerName() + ")");

        System.out.println("\n--- Creating Shipping Package ---");
        ShippingPackage pkg = new BasicShippingPackage("orderId", testAccount, 
            new BigDecimal("10.00"), new BigDecimal("10.00"), new BigDecimal("10.00"));
        System.out.println("✓ Created shipping package:");
        System.out.println("  - Order ID: orderId");
        System.out.println("  - Dimensions: 10.00 × 10.00 × 10.00 inches");
        System.out.println("  - Account: testAccount");

        System.out.println("\n--- Assigning Package to Locker ---");
        Date today = new Date();
        Date twoDaysAgo = new Date(today.getTime() - 2 * 24 * 60 * 60 * 1000);
        System.out.println("✓ Package delivery date: " + twoDaysAgo);
        Locker assignedLocker = lockerManager.assignPackage(pkg, twoDaysAgo);
        System.out.println("✓ Package assigned to locker successfully");
        assertEquals(SMALL, assignedLocker.getSize());
        assertEquals(ShippingStatus.IN_LOCKER, pkg.getStatus());
        assertFalse(assignedLocker.isAvailable());
        System.out.println("✓ Package assignment verified:");
        System.out.println("  - Assigned locker size: SMALL");
        System.out.println("  - Package status: IN_LOCKER");
        System.out.println("  - Locker is now occupied");

        System.out.println("\n--- Generating Access Code ---");
        String access = assignedLocker.getAccessCode();
        System.out.println("✓ Access code generated for package pickup");

        System.out.println("\n--- Processing Package Pickup ---");
        Locker foundLocker = lockerManager.pickUpPackage(access);
        System.out.println("✓ Package picked up using access code");
        assertTrue(foundLocker.isAvailable());
        assertEquals(foundLocker, assignedLocker);
        assertEquals(ShippingStatus.RETRIEVED, pkg.getStatus());
        System.out.println("✓ Package pickup verified:");
        System.out.println("  - Locker is now available");
        System.out.println("  - Package status: RETRIEVED");
        System.out.println("  - Correct locker accessed");

        System.out.println("\n--- Verifying Account Charges ---");
        assertTrue(BigDecimal.TEN.compareTo(testAccount.getUsageCharges()) == 0);
        System.out.println("✓ Account charges verified: $" + testAccount.getUsageCharges());
        System.out.println("=== Shipping Locker Test Completed Successfully ===\n");
    }

    private static LockerManager createLockerManager() {
        System.out.println("  - Creating locker site with 10 lockers of each size (SMALL, MEDIUM, LARGE)");
        final Map<LockerSize, Integer> lockerSizeToCountMap =
                new EnumMap<>(Map.of(
                        SMALL, 10,
                        MEDIUM, 10,
                        LARGE, 10
                ));
        final Site testSite = new Site(lockerSizeToCountMap);

        System.out.println("  - Creating test account with locker policy (0-10 lockers allowed)");
        final AccountLockerPolicy policy = new AccountLockerPolicy(0, 10);
        final Account testAccount = new Account("testAccount", "testOwnerName", policy);

        System.out.println("  - Setting up account management system");
        final Map<String, Account> accountMap = new HashMap<>(Map.of("testAccount", testAccount));

        return new LockerManager(
                testSite,
                accountMap,
                (message, user) -> {
                    // print out the email to manually verify during the interview
                }
        );
    }
}
