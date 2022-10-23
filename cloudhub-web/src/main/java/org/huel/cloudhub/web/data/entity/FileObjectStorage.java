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
    private String id;// id in meta server

    @DataColumn(name = "file_display_path")
    private String displayPath;// user defined path

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
    public FileObjectStorage(String id, String displayPath, String cachePath, long fileSize, long lastAccessTime) {
        this.id = id;
        this.displayPath = displayPath;
        this.cachePath = cachePath;
        this.fileSize = fileSize;
        this.lastAccessTime = lastAccessTime;
    }

    public FileObjectStorage(String displayPath, String cachePath, long fileSize) {
        this.displayPath = displayPath;
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

    public String getDisplayPath() {
        return displayPath;
    }

    public FileObjectStorage setDisplayPath(String displayPath) {
        this.displayPath = displayPath;
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
