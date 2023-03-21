package org.huel.cloudhub.client.data.entity.object;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
@DataTable(name = "object_versioned_table", configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public class VersionedObject {
    // TODO: Versioned Object
    public static final long INVALID_VERSION = -1L;

    @DataColumn(name = "bucket_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "200"))
    @PrimaryKey
    private String bucketName;

    @DataColumn(name = "object_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "256"))
    @PrimaryKey
    private String objectName;

    @DataColumn(name = "file_id")
    private String fileId;

    @DataColumn(name = "object_version")
    @PrimaryKey
    private long version = INVALID_VERSION;

    @DataColumn(name = "last_modified")
    private long lastModified;

    public VersionedObject(String bucketName, String objectName,
                           String fileId, long version, long lastModified) {
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.fileId = fileId;
        this.version = version;
        this.lastModified = lastModified;
    }

    public VersionedObject() {
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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
