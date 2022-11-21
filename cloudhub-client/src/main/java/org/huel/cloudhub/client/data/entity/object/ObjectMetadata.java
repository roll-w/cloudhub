package org.huel.cloudhub.client.data.entity.object;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

import java.util.Map;

/**
 * @author RollW
 */
@DataTable(tableName = "object_metadata_table")
public class ObjectMetadata {
    // TODO:
    @DataColumn(name = "user_id")
    @PrimaryKey
    private Long userId;

    @DataColumn(name = "object_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "600"))
    @PrimaryKey
    private String objectName;
    /**
     * Metadata of the object.
     */
    @DataColumn(name = "object_metadata", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "2000"))
    private Map<String, String> metadata;

    public ObjectMetadata() {
    }

    public ObjectMetadata(Long userId, String objectName, Map<String, String> metadata) {
        this.userId = userId;
        this.objectName = objectName;
        this.metadata = metadata;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
