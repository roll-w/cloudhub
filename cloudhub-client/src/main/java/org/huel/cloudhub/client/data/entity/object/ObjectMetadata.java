package org.huel.cloudhub.client.data.entity.object;

import org.huel.cloudhub.client.service.object.ObjectMetadataHeaders;
import space.lingu.NonNull;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@DataTable(tableName = "object_metadata_table")
public class ObjectMetadata {
    @DataColumn(name = "bucket_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "200"))
    @PrimaryKey
    private String bucketName;

    @DataColumn(name = "object_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "256"))
    @PrimaryKey
    private String objectName;

    /**
     * Metadata of the object.
     */
    @DataColumn(name = "object_metadata", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "2000"))
    private Map<String, String> metadata;

    public ObjectMetadata(String bucketName, String objectName, Map<String, String> metadata) {
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.metadata = metadata;
    }

    public ObjectMetadata() {
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void addAll(Map<String, String> metadata) {
        addAll(metadata, false);
    }

    public void addAll(Map<String, String> metadata, boolean force) {
        if (metadata == null) {
            return;
        }
        if (this.metadata == null || this.metadata.isEmpty()) {
            this.metadata = new HashMap<>();
        }
        if (force) {
            metadata.forEach(this::forcePutData);
            return;
        }
        metadata.forEach(this::putData);
    }

    private void putData(String k, String v) {
        if (metadata.get(k) == null) {
            metadata.put(k, v);
            return;
        }
        if (ObjectMetadataHeaders.getUnmodifiableHeaders().contains(k)) {
            return;
        }
        metadata.put(k, v);
    }

    private void forcePutData(String k, String v) {
        metadata.put(k, v);
    }

    public void removeKey(String key) {
        if (ObjectMetadataHeaders.getUnmodifiableHeaders().contains(key)) {
            return;
        }
        if (metadata == null) {
            return;
        }
        metadata.remove(key);
    }

    @NonNull
    public Map<String, String> getMetadata() {
        if (metadata == null) {
            return Collections.emptyMap();
        }
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
