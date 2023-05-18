package org.huel.cloudhub.client.disk.domain.storagepermission;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import space.lingu.NonNull;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "storage_permission", indices = {
        @Index(value = {"storage_id", "storage_type"}, unique = true)
})
public class StoragePermission implements SystemResource, DataItem, StorageIdentity {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "storage_id")
    private final long storageId;

    @DataColumn(name = "storage_type")
    private final StorageType storageType;

    @DataColumn(name = "permission_type")
    private final PublicPermissionType permissionType;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    public StoragePermission(Long id, long storageId,
                             StorageType storageType,
                             PublicPermissionType permissionType,
                             long createTime, long updateTime,
                             boolean deleted) {
        this.id = id;
        this.storageId = storageId;
        this.storageType = storageType;
        this.permissionType = permissionType;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public long getStorageId() {
        return storageId;
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    public PublicPermissionType getPermissionType() {
        return permissionType;
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

    @Override
    public long getResourceId() {
        return getId();
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.STORAGE_PERMISSION;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static StoragePermission defaultOf(StorageIdentity storageIdentity) {
        return builder()
                .setStorageId(storageIdentity.getStorageId())
                .setStorageType(storageIdentity.getStorageType())
                .setPermissionType(PublicPermissionType.PRIVATE)
                .build();
    }
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private long storageId;
        private StorageType storageType;
        private PublicPermissionType permissionType;
        private long createTime;
        private long updateTime;
        private boolean deleted;


        public Builder() {
        }

        public Builder(StoragePermission storagePermission) {
            this.id = storagePermission.id;
            this.storageId = storagePermission.storageId;
            this.storageType = storagePermission.storageType;
            this.permissionType = storagePermission.permissionType;
            this.createTime = storagePermission.createTime;
            this.updateTime = storagePermission.updateTime;
            this.deleted = storagePermission.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
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

        public Builder setPermissionType(PublicPermissionType permissionType) {
            this.permissionType = permissionType;
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

        public StoragePermission build() {
            return new StoragePermission(id,
                    storageId, storageType,
                    permissionType, createTime, updateTime, deleted);
        }
    }

}
