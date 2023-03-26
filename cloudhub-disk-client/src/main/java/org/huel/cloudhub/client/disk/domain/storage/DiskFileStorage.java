package org.huel.cloudhub.client.disk.domain.storage;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "disk_file_storage")
public class DiskFileStorage {
    @DataColumn(name = "file_id")
    @PrimaryKey
    private final String fileId;

    @DataColumn(name = "size")
    private final long fileSize;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "last_access_time", dataType = SQLDataType.TIMESTAMP)
    private final long lastAccessTime;

    public DiskFileStorage(String fileId,
                           long fileSize,
                           long createTime,
                           long lastAccessTime) {
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.createTime = createTime;
        this.lastAccessTime = lastAccessTime;
    }

    public String getFileId() {
        return fileId;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String fileId;
        private long fileSize;
        private long createTime;
        private long lastAccessTime;

        public Builder() {
        }

        public Builder(DiskFileStorage diskFileStorage) {
            this.fileId = diskFileStorage.fileId;
            this.fileSize = diskFileStorage.fileSize;
            this.createTime = diskFileStorage.createTime;
            this.lastAccessTime = diskFileStorage.lastAccessTime;
        }

        public Builder setFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder setFileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder setCreateTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setLastAccessTime(long lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
            return this;
        }

        public DiskFileStorage build() {
            return new DiskFileStorage(fileId, fileSize, createTime, lastAccessTime);
        }
    }
}
