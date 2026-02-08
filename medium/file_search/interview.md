# File Search System - LLD Interview Guide

## Problem Statement

Design a file system search utility that can efficiently search files by name, content, type, size, and other attributes across large directory trees.

## Key Requirements

- Search by filename (exact, partial, wildcard)
- Search by content (text within files)
- Filter by type, size, date modified
- Support recursive directory traversal
- Efficient indexing

## Key Classes

- `FileSearchEngine`: Main search orchestrator
- `SearchCriteria`: Search parameters
- `FileIndexer`: Builds and maintains search index
- `SearchResult`: Matched files
- `FileMetadata`: File properties

## Common Questions

**Q1: How do you efficiently search millions of files?**
- **Build Index**: Create inverted index (word → list of files)
- **Trie Data Structure**: For prefix matching on filenames
- **Metadata Cache**: Store file properties in database

**Q2: How do you handle file content search?**
```java
class FileIndexer {
    Map<String, Set<File>> invertedIndex = new HashMap<>();
    
    voidentIndexFile(File file) {
        String content = readFile(file);
        String[] words = content.split("\\s+");
        for (String word : words) {
            invertedIndex.computeIfAbsent(word, k -> new HashSet<>()).add(file);
        }
    }
    
    Set<File> searchByContent(String query) {
        return invertedIndex.getOrDefault(query, Collections.emptySet());
    }
}
```

**Q3: How would you keep the index updated?**
- **File Watcher**: Monitor file system changes (Java WatchService)
- **Incremental Indexing**: Only reindex changed files
- **Background Thread**: Periodic re-scanning

## Key Topics
- Inverted index for fast text search
- File system traversal (BFS vs DFS)
- Trie for autocomplete/prefix matching
- Caching and index maintenance
