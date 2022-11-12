package org.huel.cloudhub.client.data.entity.object;

import org.checkerframework.checker.nullness.qual.Nullable;
import space.lingu.light.*;

/**
 * @author RollW
 */
@DataTable(tableName = "file_object_storage_table", configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public class FileObjectStorage {
    public static final long INVALID_VERSION = -1L;

    /**
     * 存储桶ID。格式: 用户ID-存储桶名称
     */
    @DataColumn(name = "bucket_id")
    @PrimaryKey
    private String bucketId;

    @DataColumn(name = "object_version")
    @PrimaryKey
    private Long version = INVALID_VERSION;

    /**
     * 对象名：可以是一个完整的路径，作为Object的Key。
     */
    @PrimaryKey
    @DataColumn(name = "object_name")
    private String objectName;

    @DataColumn(name = "file_id")
    private String fileId;// id in meta server

    @DataColumn(name = "object_size")
    private long objectSize;

    public FileObjectStorage() {
    }

    public FileObjectStorage(String bucketId, long version,
                             String fileId, String objectName, long objectSize) {
        this.bucketId = bucketId;
        this.version = version;
        this.fileId = fileId;
        this.objectName = objectName;
        this.objectSize = objectSize;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getFileId() {
        return fileId;
    }

    public FileObjectStorage setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public FileObjectStorage setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public long getObjectSize() {
        return objectSize;
    }

    public FileObjectStorage setObjectSize(long objectSize) {
        this.objectSize = objectSize;
        return this;
    }

    @Nullable
    public Long getVersion() {
        if (version == null) {
            return INVALID_VERSION;
        }
        return version;
    }

    public void setVersion(@Nullable Long version) {
        if (version == null) {
            this.version = INVALID_VERSION;
            return;
        }
        this.version = version;
    }
}
