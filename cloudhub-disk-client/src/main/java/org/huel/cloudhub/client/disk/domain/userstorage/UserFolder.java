package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import space.lingu.NonNull;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;

/**
 * 用户定义的文件夹/目录
 *
 * @author RollW
 */
@DataTable(name = "user_folder", indices = {
        @Index(value = {"owner", "owner_type", "parent_id", "name"}, unique = true)
})
public class UserFolder implements AttributedStorage, DataItem {
    public static final long ROOT = 0;

    public static final UserFolder ROOT_FOLDER = new UserFolder(
            ROOT,
            ROOT,
            ROOT, LegalUserType.USER,
            "root",
            0, 0, false
    );

    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "parent_id", defaultValue = "0")
    private final Long parentId;

    @DataColumn(name = "owner")
    private final long owner;

    @DataColumn(name = "owner_type")
    private final LegalUserType ownerType;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public UserFolder(Long id, Long parentId,
                      long owner, LegalUserType ownerType,
                      String name,
                      long createTime,
                      long updateTime, boolean deleted) {
        this.id = id;
        this.parentId = parentId;
        this.owner = owner;
        this.ownerType = ownerType;
        this.name = name;
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
        return getId();
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return StorageType.FOLDER;
    }

    public Long getParentId() {
        return parentId;
    }

    @Override
    public long getOwnerId() {
        return getOwner();
    }

    public long getOwner() {
        return owner;
    }

    @NonNull
    public LegalUserType getOwnerType() {
        return ownerType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FileType getFileType() {
        return FileType.OTHER;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long parentId;
        private long owner;
        private LegalUserType ownerType;
        private String name;
        private long createTime;
        private long updateTime;
        private boolean deleted;

        public Builder() {
        }

        public Builder(UserFolder userFolder) {
            this.id = userFolder.id;
            this.parentId = userFolder.parentId;
            this.owner = userFolder.owner;
            this.ownerType = userFolder.ownerType;
            this.name = userFolder.name;
            this.createTime = userFolder.createTime;
            this.updateTime = userFolder.updateTime;
            this.deleted = userFolder.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setOwner(long owner) {
            this.owner = owner;
            return this;
        }

        public Builder setOwnerType(LegalUserType ownerType) {
            this.ownerType = ownerType;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
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

        public UserFolder build() {
            return new UserFolder(id, parentId, owner, ownerType,
                    name, createTime, updateTime, deleted);
        }
    }

}
