package org.huel.cloudhub.client.data.entity.object;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
@DataTable(tableName = "object_versioned_table")
public class VersionedObject {
    // TODO: Versioned Object
    public static final long INVALID_VERSION = -1L;

    @DataColumn(name = "bucket_id")
    @PrimaryKey
    private String bucketId;

    @DataColumn(name = "file_id")
    @PrimaryKey
    private String fileId;

    @DataColumn(name = "object_version")
    @PrimaryKey
    private long version = INVALID_VERSION;

    public VersionedObject(String bucketId, String fileId, long version) {
        this.bucketId = bucketId;
        this.fileId = fileId;
        this.version = version;
    }

    public VersionedObject() {
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

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
