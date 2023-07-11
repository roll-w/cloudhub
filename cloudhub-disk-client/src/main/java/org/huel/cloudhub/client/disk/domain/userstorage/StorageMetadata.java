package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.database.DataItem;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
@DataTable(name = "storage_metadata", indices = {
        @Index({"storage_id", "tag_group_id", "tag_id"}),
})
public class StorageMetadata implements DataItem {
    public static final long INVALID_TAG_ID = -1;

    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "storage_id")
    private final long storageId;

    @DataColumn(name = "tag_group_id")
    private final long tagGroupId;

    @DataColumn(name = "tag_id")
    private final long tagId;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    public StorageMetadata(Long id, long storageId,
                           long tagGroupId, long tagId,
                           boolean deleted,
                           long createTime, long updateTime) {
        this.id = id;
        this.storageId = storageId;
        this.tagGroupId = tagGroupId;
        this.tagId = tagId;
        this.deleted = deleted;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getStorageId() {
        return storageId;
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
                    id, storageId,
                    tagGroupId, tagId,
                    deleted, createTime, updateTime);
        }
    }
}
