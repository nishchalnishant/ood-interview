# Amazon Locker System

This project is an object-oriented design and implementation of an Amazon Locker system in Java. It includes core classes for lockers, packages, accounts, and a JUnit test suite.

## Prerequisites

- Java JDK 8 or higher
- Internet connection (for downloading JUnit dependencies)

## Project Structure

```
shippinglocker/
  account/         # Account-related classes
  locker/          # Locker-related classes
  pkg/             # Package-related classes
  ShippingLockerTest.java   # JUnit test class
  Site.java
  NotificationInterface.java
  EmailNotification.java
```

## Running the Tests

```bash
# From repository root
./gradlew :shippinglocker:test
```

## Notes

- No build system (like Maven or Gradle) is required.
- If you add new test files, simply recompile and rerun the above commands.
- The test output will be shown in the terminal. 
