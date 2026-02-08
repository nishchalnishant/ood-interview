import clause.AndPredicate;
import clause.SimplePredicate;
import filesystem.File;
import filesystem.FileAttribute;
import operator.EqualsOperator;
import operator.RegexMatchOperator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileSearchTest {
        @Test
        public void testFileSearch() {
                System.out.println("\n=== Testing File Search System ===");
                
                System.out.println("\n--- Creating File System Structure ---");
                final File root = new File(true, 0, "adam", "root");
                final File a = new File(false, 2000, "adam", "a");
                final File b = new File(false, 3000, "george", "b");
                System.out.println("✓ Created root directory: 'root' (owner: adam, size: 0)");
                System.out.println("✓ Created file 'a': (owner: adam, size: 2000, isDirectory: false)");
                System.out.println("✓ Created file 'b': (owner: george, size: 3000, isDirectory: false)");

                root.addEntry(a);
                root.addEntry(b);
                System.out.println("✓ Added files 'a' and 'b' to root directory");

                System.out.println("\n--- Building Search Criteria ---");
                System.out.println("✓ Creating AND predicate with two conditions:");
                System.out.println("  1. File must NOT be a directory (isDirectory = false)");
                System.out.println("  2. Owner name must match regex pattern 'ge.*' (starts with 'ge')");
                
                final FileSearchCriteria criteria = new FileSearchCriteria(
                                new AndPredicate(
                                                List.of(
                                                                new SimplePredicate<>(
                                                                                FileAttribute.IS_DIRECTORY,
                                                                                new EqualsOperator<>(),
                                                                                false),
                                                                new SimplePredicate<>(
                                                                                FileAttribute.OWNER,
                                                                                new RegexMatchOperator<>(),
                                                                                "ge.*"))));
                System.out.println("✓ Search criteria created successfully");

                System.out.println("\n--- Executing File Search ---");
                final FileSearch fileSearch = new FileSearch();
                final List<File> result = fileSearch.search(root, criteria);
                System.out.println("✓ Search completed");
                System.out.println("✓ Found " + result.size() + " file(s) matching criteria");

                System.out.println("\n--- Analyzing Results ---");
                System.out.println("✓ Expected: Only file 'b' should match because:");
                System.out.println("  - File 'a': owner='adam' (doesn't match 'ge.*')");
                System.out.println("  - File 'b': owner='george' (matches 'ge.*') and is not a directory");
                
                assertEquals(1, result.size());
                assertEquals("b", result.get(0).getFilename());
                System.out.println("✓ Result verified: Found 1 file");
                System.out.println("✓ File name verified: 'b'");
                System.out.println("=== File Search Test Completed Successfully ===\n");
        }
}
