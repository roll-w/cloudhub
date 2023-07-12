package org.huel.cloudhub.client.disk.domain.favorites;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;

/**
 * @author RollW
 */
@DataTable(name = "favorite_item")
public class FavoriteItem implements DataItem {
    @DataColumn(name = "id")
    private final Long id;

    @DataColumn(name = "favorite_group_id")
    private final long favoriteGroupId;

    @DataColumn(name = "userId")
    private final long userId;

    @DataColumn(name = "storage_id")
    private final long storageId;

    @DataColumn(name = "storage_type")
    private final StorageType storageType;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public FavoriteItem(Long id, long favoriteGroupId, long userId,
                        long storageId, StorageType storageType,
                        long createTime, long updateTime, boolean deleted) {
        this.id = id;
        this.favoriteGroupId = favoriteGroupId;
        this.userId = userId;
        this.storageId = storageId;
        this.storageType = storageType;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getFavoriteGroupId() {
        return favoriteGroupId;
    }

    public long getUserId() {
        return userId;
    }

    public long getStorageId() {
        return storageId;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        private Long id;
        private long favoriteGroupId;
        private long userId;
        private long storageId;
        private StorageType storageType;
        private long createTime;
        private long updateTime;
        private boolean deleted;

        private Builder() {
        }

        private Builder(FavoriteItem favoriteitem) {
            this.id = favoriteitem.id;
            this.favoriteGroupId = favoriteitem.favoriteGroupId;
            this.userId = favoriteitem.userId;
            this.storageId = favoriteitem.storageId;
            this.storageType = favoriteitem.storageType;
            this.createTime = favoriteitem.createTime;
            this.updateTime = favoriteitem.updateTime;
            this.deleted = favoriteitem.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setFavoriteGroupId(long favoriteGroupId) {
            this.favoriteGroupId = favoriteGroupId;
            return this;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setStorageId(long storageId) {
            this.storageId = storageId;
            return this;
        }

        public Builder setStorageType(StorageType storageType) {
            this.storageType = storageType;
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

        public Builder setDeleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public FavoriteItem build() {
            return new FavoriteItem(id, favoriteGroupId, userId, storageId, storageType, createTime, updateTime, deleted);
        }
    }
}
