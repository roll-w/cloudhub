package org.huel.cloudhub.web.data.entity;

import space.lingu.light.*;

/**
 * @author RollW
 */
@DataTable(tableName = "file_object_storage_table", configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public class FileObjectStorage {
    @DataColumn(name = "file_id")
    @PrimaryKey
    private String id;// md5

    @DataColumn(name = "file_path")
    private String path;

    @DataColumn(name = "file_cache_path", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "255"))
    private String cachePath;

    @DataColumn(name = "file_size")
    private long fileSize;

    @DataColumn(name = "file_last_access_time")
    private long lastAccessTime;

    public FileObjectStorage() {
    }

    @Constructor
    public FileObjectStorage(String id, String path, String cachePath, long fileSize, long lastAccessTime) {
        this.id = id;
        this.path = path;
        this.cachePath = cachePath;
        this.fileSize = fileSize;
        this.lastAccessTime = lastAccessTime;
    }

    public FileObjectStorage(String path, String cachePath, long fileSize) {
        this.path = path;
        this.cachePath = cachePath;
        this.fileSize = fileSize;
    }

    public String getId() {
        return id;
    }

    public FileObjectStorage setId(String id) {
        this.id = id;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FileObjectStorage setPath(String path) {
        this.path = path;
        return this;
    }

    public String getCachePath() {
        return cachePath;
    }

    public FileObjectStorage setCachePath(String cachePath) {
        this.cachePath = cachePath;
        return this;
    }

    public long getFileSize() {
        return fileSize;
    }

    public FileObjectStorage setFileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public FileObjectStorage setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }
}
