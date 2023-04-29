package org.huel.cloudhub.client.disk.domain.storagepermission;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "storage_permission")
public class StoragePermission implements SystemResource {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "storage_id")
    private final long storageId;

    @DataColumn(name = "storage_type")
    private final StorageType storageType;

    @DataColumn(name = "owner_id")
    private final long ownerId;

    @DataColumn(name = "legal_user_type")
    private final LegalUserType legalUserType;

    @DataColumn(name = "permission_type")
    private final PermissionType permissionType;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    public StoragePermission(Long id, long storageId,
                             StorageType storageType,
                             long ownerId,
                             LegalUserType legalUserType,
                             PermissionType permissionType,
                             long createTime, long updateTime,
                             boolean deleted) {
        this.id = id;
        this.storageId = storageId;
        this.storageType = storageType;
        this.ownerId = ownerId;
        this.legalUserType = legalUserType;
        this.permissionType = permissionType;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public long getStorageId() {
        return storageId;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public LegalUserType getLegalUserType() {
        return legalUserType;
    }

    public PermissionType getPermissionType() {
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private long storageId;
        private StorageType storageType;
        private long ownerId;
        private LegalUserType legalUserType;
        private PermissionType permissionType;
        private long createTime;
        private long updateTime;
        private boolean deleted;


        public Builder() {
        }

        public Builder(StoragePermission storagePermission) {
            this.id = storagePermission.id;
            this.storageId = storagePermission.storageId;
            this.storageType = storagePermission.storageType;
            this.ownerId = storagePermission.ownerId;
            this.legalUserType = storagePermission.legalUserType;
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

        public Builder setOwnerId(long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder setLegalUserType(LegalUserType legalUserType) {
            this.legalUserType = legalUserType;
            return this;
        }

        public Builder setPermissionType(PermissionType permissionType) {
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
            return new StoragePermission(id, storageId, storageType, ownerId, legalUserType, permissionType, createTime, updateTime, deleted);
        }
    }

}
