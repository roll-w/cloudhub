package org.huel.cloudhub.client.data.entity.object;

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
    @DataColumn(name = "user_id", configuration =
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
        if (metadata == null) {
            return;
        }
        if (this.metadata == null) {
            this.metadata = new HashMap<>(metadata);
            return;
        }
        this.metadata.putAll(metadata);
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
