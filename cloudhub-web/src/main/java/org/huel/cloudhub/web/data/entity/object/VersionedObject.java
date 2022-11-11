package org.huel.cloudhub.web.data.entity.object;

import space.lingu.light.DataColumn;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
public class VersionedObject {
    // TODO: Versioned Object
    public static final long INVALID_VERSION = -1L;

    private String bucketId;

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
