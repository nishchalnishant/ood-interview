package filesystem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Represents a file or directory in the file system
// Contains basic file attributes and supports hierarchical structure
public class File {
    private final boolean isDirectory;
    private final int size;
    private final String owner;
    private final String filename;
    // Set of directory entries (files and subdirectories)
    private final Set<File> entries = new HashSet<>();

    // Creates a new file with the specified attributes
    public File(final boolean isDirectory, final int size, final String owner, final String filename) {
        this.isDirectory = isDirectory;
        this.size = size;
        this.owner = owner;
        this.filename = filename;
    }

    // Extracts the value of a specified file attribute
    public Object extract(final FileAttribute attributeName) {
        switch (attributeName) {
            case SIZE -> {
                return size;
            }
            case OWNER -> {
                return owner;
            }
            case IS_DIRECTORY -> {
                return isDirectory;
            }
            case FILENAME -> {
                return filename;
            }
        }
        throw new IllegalArgumentException("invalid filter criteria type");
    }

    // Adds a file or directory entry to this directory
    public void addEntry(final File entry) {
        entries.add(entry);
    }

    public Set<File> getEntries() {
        return Collections.unmodifiableSet(entries);
    }

    public String getFilename() {
        return filename;
    }

    // getter methods omitted for brevity
}
