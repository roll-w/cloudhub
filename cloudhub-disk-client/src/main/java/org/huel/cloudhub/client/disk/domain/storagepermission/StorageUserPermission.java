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

import java.util.List;

/**
 * @author RollW
 */
@DataTable(name = "storage_user_permission", indices = {
        @Index(value = {"storage_id", "storage_type", "user_id"}, unique = true),
})
public class StorageUserPermission implements SystemResource, DataItem, StorageIdentity {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "storage_id")
    private final long storageId;

    @DataColumn(name = "storage_type")
    private final StorageType storageType;

    @DataColumn(name = "permission_types")
    private final List<PermissionType> permissionTypes;

    @DataColumn(name = "user_id")
    private final long userId;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    public StorageUserPermission(Long id, long storageId,
                                 StorageType storageType,
                                 List<PermissionType> permissionTypes,
                                 long userId, long createTime, long updateTime,
                                 boolean deleted) {
        this.id = id;
        this.storageId = storageId;
        this.storageType = storageType;
        this.permissionTypes = permissionTypes;
        this.userId = userId;
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

    public long getUserId() {
        return userId;
    }

    public List<PermissionType> getPermissionTypes() {
        return permissionTypes;
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
        return SystemResourceKind.STORAGE_USER_PERMISSION;
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
        private StorageType storageType;
        private List<PermissionType> permissionTypes;
        private long userId;
        private long createTime;
        private long updateTime;
        private boolean deleted;


        public Builder() {
        }

        public Builder(StorageUserPermission storagePermission) {
            this.id = storagePermission.id;
            this.storageId = storagePermission.storageId;
            this.storageType = storagePermission.storageType;
            this.permissionTypes = storagePermission.permissionTypes;
            this.userId = storagePermission.userId;
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

        public Builder setPermissionTypes(List<PermissionType> permissionTypes) {
            this.permissionTypes = permissionTypes;
            return this;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
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

        public StorageUserPermission build() {
            return new StorageUserPermission(id, storageId, storageType,
                    permissionTypes, userId,
                    createTime, updateTime, deleted);
        }
    }

}
