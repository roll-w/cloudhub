package org.huel.cloudhub.client.disk.domain.userstorage;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "storage_metadata", indices = {
        @Index({"storage_id", "tag_group_id", "tag_id"}),
})
public class StorageMetadata {
    public static final long INVALID_TAG_ID = -1;

    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "storage_id")
    private final long storageId;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "value")
    private final String value;

    @DataColumn(name = "tag_group_id")
    private final long tagGroupId;

    @DataColumn(name = "tag_id")
    private final long tagId;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    public StorageMetadata(Long id, long storageId,
                           String name, String value,
                           long tagGroupId, long tagId,
                           boolean deleted, long createTime, long updateTime) {
        this.id = id;
        this.storageId = storageId;
        this.name = name;
        this.value = value;
        this.tagGroupId = tagGroupId;
        this.tagId = tagId;
        this.deleted = deleted;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public long getStorageId() {
        return storageId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public long getTagGroupId() {
        return tagGroupId;
    }

    public long getTagId() {
        return tagId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private long storageId;
        private String name;
        private String value;
        private long tagGroupId;
        private long tagId;
        private boolean deleted;
        private long createTime;
        private long updateTime;

        public Builder() {
        }

        public Builder(StorageMetadata storageMetadata) {
            this.id = storageMetadata.id;
            this.storageId = storageMetadata.storageId;
            this.name = storageMetadata.name;
            this.value = storageMetadata.value;
            this.tagGroupId = storageMetadata.tagGroupId;
            this.tagId = storageMetadata.tagId;
            this.deleted = storageMetadata.deleted;
            this.createTime = storageMetadata.createTime;
            this.updateTime = storageMetadata.updateTime;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setStorageId(long storageId) {
            this.storageId = storageId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setTagGroupId(long tagGroupId) {
            this.tagGroupId = tagGroupId;
            return this;
        }

        public Builder setTagId(long tagId) {
            this.tagId = tagId;
            return this;
        }

        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Builder setCreateTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public StorageMetadata build() {
            return new StorageMetadata(
                    id, storageId, name, value,
                    tagGroupId, tagId,
                    deleted, createTime, updateTime);
        }
    }
}
