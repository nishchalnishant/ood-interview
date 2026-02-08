# Unix File Search Project

This project implements a flexible file search system similar to Unix's find command, allowing users to search for files based on various criteria such as name, owner, size, and directory status.

## Project Structure

```
filesearch/
├── FileSearch.java           # Main search implementation
├── FileSearchCriteria.java   # Search criteria definition
├── FileSearchTest.java       # Unit tests
├── clause/                   # Search predicates
│   ├── AndPredicate.java
│   ├── OrPredicate.java
│   └── SimplePredicate.java
├── operator/                 # Comparison operators
└── filesystem/              # File system related classes
```

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- JUnit 5 (will be downloaded automatically during setup)

## Running the Tests

```bash
# From repository root
./gradlew :filesearch:test
```

## Usage Example

The file search system supports various search criteria that can be combined using AND/OR predicates. Here's an example of how to use it:

```java
// Create a file hierarchy
File root = new File(true, 0, "adam", "root");
File a = new File(false, 2000, "adam", "a");
root.addEntry(a);

// Create search criteria
FileSearchCriteria criteria = new FileSearchCriteria(
    new SimplePredicate<>(
        FileAttribute.IS_DIRECTORY,
        new EqualsOperator<>(),
        false
    )
);

// Perform the search
FileSearch fileSearch = new FileSearch();
List<File> results = fileSearch.search(root, criteria);
```

## Search Criteria

The system supports the following search criteria:
- File name matching (exact match or regex)
- File owner
- File size
- Directory status
- Combinations of criteria using AND/OR operators

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is open source and available under the MIT License. 
