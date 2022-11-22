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
    /**
     * 存储桶ID
     */
    @DataColumn(name = "bucket_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "200"))
    @PrimaryKey
    private String bucketName;

    /**
     * 对象名：可以是一个完整的路径，作为Object的Key。
     */
    @PrimaryKey
    @DataColumn(name = "object_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "256"))
    private String objectName;

    @DataColumn(name = "file_id")
    private String fileId;// id in meta server

    @DataColumn(name = "object_size")
    private long objectSize;

    @DataColumn(name = "object_create_time")
    private long createTime;

    public FileObjectStorage() {
    }

    public FileObjectStorage(String bucketName, String objectName, String fileId,
                             long objectSize, long createTime) {
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.fileId = fileId;
        this.objectSize = objectSize;
        this.createTime = createTime;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public long getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(long objectSize) {
        this.objectSize = objectSize;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
