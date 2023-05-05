package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import space.lingu.NonNull;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * 用户定义的文件夹/目录
 *
 * @author RollW
 */
@DataTable(name = "user_directory")
public class UserDirectory implements AttributedStorage, DataItem {
    public static final long ROOT = 0;

    public static final UserDirectory ROOT_DIRECTORY = new UserDirectory(
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

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public UserDirectory(Long id, Long parentId,
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
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

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

        public Builder(UserDirectory userDirectory) {
            this.id = userDirectory.id;
            this.parentId = userDirectory.parentId;
            this.owner = userDirectory.owner;
            this.ownerType = userDirectory.ownerType;
            this.name = userDirectory.name;
            this.createTime = userDirectory.createTime;
            this.updateTime = userDirectory.updateTime;
            this.deleted = userDirectory.deleted;
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

        public UserDirectory build() {
            return new UserDirectory(id, parentId, owner, ownerType,
                    name, createTime, updateTime, deleted);
        }
    }

}
