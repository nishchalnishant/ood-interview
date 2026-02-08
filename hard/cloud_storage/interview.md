# Cloud Storage (Dropbox/Google Drive) - LLD Interview Guide

## Problem Statement

Design a cloud file storage and synchronization service like Dropbox or Google Drive that allows users to upload, download, share files, and automatically sync files across multiple devices.

## Clarifying Questions

1. **Storage Limits**: Per-user storage quota? Max file size?
2. **Sync**: Real-time sync or periodic? Conflict resolution strategy?
3. **Sharing**: File/folder sharing with permissions (view/edit)?
4. **Versioning**: Keep file history? How many versions?
5. **Platform**: Web, mobile, desktop clients?
6. **Offline**: Offline access and automatic sync when online?
7. **Scale**: Number of users? Files uploaded per day?

## Core Requirements

### Functional Requirements
- Upload/download files
- Create/delete folders
- File/folder sharing with permissions
- Automatic file synchronization across devices
- File versioning (keep history)
- Conflict resolution (same file edited on 2 devices)
- Search files by name/content
- Notifications on file changes

### Non-Functional Requirements
- **Scalability**: Handle millions of users
- **Availability**: 99.99% uptime
- **Consistency**: Eventual consistency acceptable
- **Performance**: Upload/download with chunking for large files
- **Security**: Encryption at rest and in transit

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Client Devices                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐             │
│  │ Desktop  │  │  Mobile  │  │   Web    │             │
│  │  Client  │  │  Client  │  │  Client  │             │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘             │
└───────┼─────────────┼─────────────┼────────────────────┘
        │             │             │
        └─────────────┴─────────────┘
                      │
        ┌─────────────▼──────────────┐
        │     API Gateway /          │
        │     Load Balancer          │
        └─────────────┬──────────────┘
                      │
        ┌─────────────▼──────────────┐
        │    Application Servers     │
        │  ┌──────────┬──────────┐   │
        │  │Metadata  │  Block   │   │
        │  │ Service  │ Service  │   │
        │  └──────────┴──────────┘   │
        └───┬────────────────┬───────┘
            │                │
    ┌───────▼──────┐  ┌─────▼────────┐
    │  Metadata DB │  │ Block Storage│
    │  (SQL/NoSQL) │  │  (S3/Blob)   │
    └──────────────┘  └──────────────┘
            │                │
    ┌───────▼──────┐  ┌─────▼────────┐
    │    Cache     │  │     CDN      │
    │   (Redis)    │  │              │
    └──────────────┘  └──────────────┘
```

## Key Classes

```java
class User {
    private String userId;
    private String username;
    private String email;
    private long storageQuota;      // bytes
    private long storageUsed;       // bytes
}

class File {
    private String fileId;
    private String name;
    private String ownerId;
    private long size;
    private String mimeType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private FileMetadata metadata;
    
    // Block-based storage
    private List<String> blockIds;  // References to blocks in storage
    
    // Versioning
    private List<FileVersion> versions;
}

class FileVersion {
    private String versionId;
    private String fileId;
    private LocalDateTime timestamp;
    private String modifiedBy;
    private List<String> blockIds;
}

class Folder {
    private String folderId;
    private String name;
    private String ownerId;
    private String parentFolderId;
    private LocalDateTime createdAt;
}

class Block {
    private String blockId;      // Hash of content (SHA-256)
    private byte[] data;         // Typically 4MB chunks
    private int size;
    
    // Deduplication: Same hash = same block
}

class SyncMetadata {
    private String deviceId;
    private String fileId;
    private long lastSyncedVersion;
    private LocalDateTime lastSyncTime;
}

class Sharing {
    private String sharingId;
    private String fileId;
    private String ownerId;
    private String sharedWithUserId;
    private Permission permission;     // READ, WRITE
    private LocalDateTime expiresAt;
}
```

## Core Design Concepts

### 1. Chunking (Block-Based Storage)

**Why?**
- Efficient uploads (resume on failure)
- Bandwidth optimization (only upload changed chunks)
- Deduplication (same chunk shared across files)

```java
class ChunkingService {
    private static final int CHUNK_SIZE = 4 * 1024 * 1024; // 4 MB
    
    public List<Block> splitFile(File file) {
        List<Block> blocks = new ArrayList<>();
        byte[] fileData = readFile(file);
        
        for (int i = 0; i < fileData.length; i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, fileData.length);
            byte[] chunkData = Arrays.copyOfRange(fileData, i, end);
            
            // Content-based hashing
            String blockId = SHA256(chunkData);
            
            // Check if block already exists (deduplication)
            if (!blockStorage.exists(blockId)) {
                blockStorage.save(blockId, chunkData);
            }
            
            blocks.add(new Block(blockId, chunkData));
        }
        
        return blocks;
    }
    
    public byte[] assembleFile(List<String> blockIds) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (String blockId : blockIds) {
            byte[] chunk = blockStorage.fetch(blockId);
            output.write(chunk);
        }
        return output.toByteArray();
    }
}
```

### 2. File Synchronization

**Client-Server Sync Protocol:**

```java
class SynchronizationService {
    
    // Client sends delta (what changed)
    public void sync(String deviceId, List<FileChange> changes) {
        for (FileChange change : changes) {
            switch (change.getType()) {
                case CREATED:
                    createFile(change.getFile());
                    break;
                case MODIFIED:
                    updateFile(change.getFile(), change.getModifiedBlocks());
                    break;
                case DELETED:
                    deleteFile(change.getFileId());
                    break;
            }
        }
        
        // Notify other devices of this user
        notifyOtherDevices(deviceId, changes);
    }
    
    private void updateFile(File file, List<Block> modifiedBlocks) {
        // Only upload changed blocks
        for (Block block : modifiedBlocks) {
            if (!blockStorage.exists(block.getBlockId())) {
                blockStorage.save(block.getBlockId(), block.getData());
            }
        }
        
        // Update file metadata
        file.setModifiedAt(LocalDateTime.now());
        file.setBlockIds(extractBlockIds(modifiedBlocks));
        fileRepository.update(file);
        
        // Create new version
        createVersion(file);
    }
}
```

### 3. Conflict Resolution

**Scenario**: Same file edited on 2 devices while offline

**Strategies:**

**Option 1: Last Write Wins (LWW)**
```java
if (clientVersion.timestamp > serverVersion.timestamp) {
    // Client wins
    overwriteServerVersion(clientVersion);
} else {
    // Server wins, notify client
    sendUpdateToClient(serverVersion);
}
```

**Option 2: Create Conflict Copy**
```java
if (conflict detected) {
    // Keep both versions
    saveAs(serverFile, "file.txt");
    saveAs(clientFile, "file (conflicted copy).txt");
    notifyUser("Conflict detected, created copy");
}
```

**Option 3: Merge (if possible)**
- For text files: 3-way merge (like Git)
- For binary files: Usually not possible

### 4. Deduplication

**Content-Based Hashing:**
```java
class DeduplicationService {
    
    public String uploadFile(byte[] fileData) {
        List<Block> blocks = chunkingService.splitFile(fileData);
        List<String> blockIds = new ArrayList<>();
        
        for (Block block : blocks) {
            String blockId = SHA256(block.getData());
            
            // Only upload if block doesn't exist
            if (!blockStorage.exists(blockId)) {
                blockStorage.save(blockId, block.getData());
                // Saved bandwidth: block already exists
            }
            
            blockIds.add(blockId);
        }
        
        // Create file metadata linking to blocks
        String fileId = UUID.randomUUID().toString();
        File file = new File(fileId, blockIds);
        fileRepository.save(file);
        
        return fileId;
    }
}
```

**Benefits:**
- Save storage (same file uploaded by multiple users = one copy)
- Save bandwidth (don't re-upload existing blocks)

## Common Interview Questions

**Q1: How do you handle large file uploads?**

1. **Chunking**: Split into 4MB chunks
2. **Parallel Upload**: Upload chunks concurrently
3. **Resume**: Track uploaded chunks, resume from last chunk
4. **Generate pre-signed URLs** for direct S3 upload

```java
class UploadService {
    public UploadSession initiateUpload(String filename, long size) {
        int numChunks = (int) Math.ceil(size / CHUNK_SIZE);
        UploadSession session = new UploadSession(filename, numChunks);
        
        // Generate pre-signed URLs for each chunk
        for (int i = 0; i < numChunks; i++) {
            String url = s3.generatePresignedUrl(filename, i, expiresIn(1 hour));
            session.addChunkUrl(i, url);
        }
        
        return session;
    }
    
    public void completeUpload(String sessionId, List<String> uploadedChunkIds) {
        // Verify all chunks uploaded
        // Assemble file metadata
        // Notify user
    }
}
```

**Q2: How do you implement file sharing?**

```java
class SharingService {
    public String shareFile(String fileId, String sharedWithEmail, Permission permission) {
        // Generate shareable link
        String shareToken = generateSecureToken();
        
        Sharing sharing = new Sharing(
            shareToken,
            fileId, 
            currentUser.getId(),
            getUserId(sharedWithEmail),
            permission,
            LocalDateTime.now().plusDays(30) // Expires in 30 days
        );
        
        sharingRepository.save(sharing);
        
        // Send email notification
        emailService.send(sharedWithEmail, "File shared: " + getFileUrl(shareToken));
        
        return shareToken;
    }
    
    public File accessSharedFile(String shareToken) {
        Sharing sharing = sharingRepository.findByToken(shareToken);
        
        // Check expiration
        if (sharing.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ShareExpiredException();
        }
        
        // Check permission
        if (sharing.getPermission() == Permission.READ) {
            return readOnlyFile(sharing.getFileId());
        } else {
            return editableFile(sharing.getFileId());
        }
    }
}
```

**Q3: How do you implement file versioning?**

```java
class VersioningService {
    private static final int MAX_VERSIONS = 30;
    
    public void createVersion(File file) {
        FileVersion version = new FileVersion(
            UUID.randomUUID().toString(),
            file.getFileId(),
            file.getModifiedAt(),
            getCurrentUserId(),
            file.getBlockIds()
        );
        
        versionRepository.save(version);
        
        // Cleanup old versions
        List<FileVersion> versions = versionRepository.getVersions(file.getFileId());
        if (versions.size() > MAX_VERSIONS) {
            // Delete oldest versions
            versions.stream()
                .sorted(Comparator.comparing(FileVersion::getTimestamp))
                .limit(versions.size() - MAX_VERSIONS)
                .forEach(v -> versionRepository.delete(v));
        }
    }
    
    public File restoreVersion(String fileId, String versionId) {
        FileVersion version = versionRepository.get(versionId);
        File file = fileRepository.get(fileId);
        
        // Restore blocks from version
        file.setBlockIds(version.getBlockIds());
        file.setModifiedAt(LocalDateTime.now());
        
        fileRepository.update(file);
        return file;
    }
}
```

**Q4: How do you scale the metadata service?**

1. **Database Sharding**: Partition by user_id
2. **Caching**: Redis cache for frequently accessed files
3. **Read Replicas**: Multiple read-only databases
4. **Denormalization**: Store file count, folder size directly

**Q5: How do you ensure security?**

- **Encryption at Rest**: AES-256 encryption for blocks in S3
- **Encryption in Transit**: TLS/HTTPS for all API calls
- **Access Control**: JWT tokens, OAuth 2.0
- **Audit Logs**: Track all file access
- **Virus Scanning**: Scan uploaded files

## Database Schema

```sql
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    email VARCHAR(100) UNIQUE,
    storage_quota BIGINT,
    storage_used BIGINT
);

CREATE TABLE files (
    file_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    owner_id VARCHAR(36),
    folder_id VARCHAR(36),
    size BIGINT,
    mime_type VARCHAR(100),
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    block_ids JSON,  -- List of block IDs
    FOREIGN KEY (owner_id) REFERENCES users(user_id),
    INDEX idx_owner_folder (owner_id, folder_id)
);

CREATE TABLE file_versions (
    version_id VARCHAR(36) PRIMARY KEY,
    file_id VARCHAR(36),
    timestamp TIMESTAMP,
    modified_by VARCHAR(36),
    block_ids JSON,
    FOREIGN KEY (file_id) REFERENCES files(file_id)
);

CREATE TABLE sharing (
    sharing_id VARCHAR(36) PRIMARY KEY,
    file_id VARCHAR(36),
    owner_id VARCHAR(36),
    shared_with_user_id VARCHAR(36),
    permission ENUM('READ', 'WRITE'),
    expires_at TIMESTAMP,
    FOREIGN KEY (file_id) REFERENCES files(file_id)
);

-- Block storage is in S3/Blob storage, not SQL
```

## Real-World Considerations

- **CDN**: Serve files from edge locations (CloudFront)
- **Compression**: Compress blocks before upload (gzip, br)
- **Delta Sync**: Only upload changed bytes (rsync algorithm)
- **Mobile Optimization**: Upload photos only on WiFi
- **Offline Mode**: Queue operations, sync when online
- **Monitoring**: Track upload/download speeds, error rates

---

**Key Interview Topics:**
- Chunking for efficient uploads
- Deduplication to save storage
- Sync protocol and conflict resolution
- File versioning
- Sharing with permissions
- Scaling metadata and storage
- Security (encryption, access control)
