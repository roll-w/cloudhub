package org.huel.cloudhub.client.data.entity.object;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
@DataTable(tableName = "file_object_storage_table", configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public class FileObjectStorage {
    public static final long INVALID_VERSION = -1L;

    /**
     * 存储桶ID
     */
    @DataColumn(name = "bucket_id")
    @PrimaryKey
    private String bucketId;

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

    public FileObjectStorage(String bucketId,
                             String fileId,
                             String objectName, long objectSize) {
        this.bucketId = bucketId;
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
}
