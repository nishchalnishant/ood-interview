# Restaurant Management System

This project is a simple Restaurant Management System implemented in Java. It supports table reservations, walk-ins, menu management, and order processing.

## Prerequisites
- Java JDK 11 or higher
- Bash shell (for running the provided script)
- Internet connection (for the first run, to download JUnit dependencies)

## Project Structure
- `restaurant/` - Main source code (Java packages for menu, reservation, table, etc.)
- `run_tests.sh` - Script to compile and run the tests
- `README.md` - This file

## Running the Tests

```bash
# From repository root
./gradlew :restaurant:test
```

#### Example Output
```
Compiling source files...
Running RestaurantTest...

Thanks for using JUnit! Support its development at https://junit.org/sponsoring

╷
├─ JUnit Jupiter ✔
│  └─ RestaurantTest ✔
│     ├─ testRestaurantReservation() ✔
│     └─ testRestaurantWalkIn() ✔
...
[         2 tests found           ]
[         2 tests successful      ]
[         0 tests failed          ]
```

## Troubleshooting
- If you see errors about missing JUnit classes in your editor, but the script works, this is normal. The script downloads and uses the correct JUnit jars at runtime.
- If you get a permissions error, make the script executable:
  ```sh
  chmod +x run_tests.sh
  ```
- If you want to re-download dependencies, delete the `lib/` directory and re-run the script.

## Notes
- The main business logic is in the `restaurant/` directory, organized by feature (menu, reservation, table, etc.).
- The project does **not** use Maven or Gradle; all dependencies are managed by the script.
- You can add your own tests to `restaurant/RestaurantTest.java` or create new test files and run them similarly.
