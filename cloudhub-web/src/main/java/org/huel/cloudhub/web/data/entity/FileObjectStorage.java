package org.huel.cloudhub.web.data.entity;

import space.lingu.light.*;

/**
 * @author RollW
 */
@DataTable(tableName = "file_object_storage_table", configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public class FileObjectStorage {
    /**
     * 存储桶ID。格式: 用户ID-存储桶名称
     */
    @DataColumn(name = "bucket_id")
    @PrimaryKey
    private String bucketId;

    @DataColumn(name = "file_id")
    private String fileId;// id in meta server

    @PrimaryKey
    @DataColumn(name = "file_display_path")
    private String objectName;// user defined path

    @DataColumn(name = "file_size")
    private long fileSize;

    public FileObjectStorage() {
    }

    public FileObjectStorage(String bucketId, String fileId, String objectName, long fileSize) {
        this.bucketId = bucketId;
        this.fileId = fileId;
        this.objectName = objectName;
        this.fileSize = fileSize;
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

    public long getFileSize() {
        return fileSize;
    }

    public FileObjectStorage setFileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

}
