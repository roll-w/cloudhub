package org.huel.cloudhub.client.disk.domain.userstorage;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * 用户定义的文件夹/目录
 *
 * @author RollW
 */
@DataTable(name = "user_directory", indices = {
        @Index(value = {"name", "parent_id"}, unique = true)
})
public class UserDirectory {
    public static final long ROOT = 0;
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "parent_id")
    private final Long parentId;

    @DataColumn(name = "owner")
    private final long owner;

    @DataColumn(name = "owner_type")
    private final OwnerType ownerType;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    public UserDirectory(Long id, Long parentId,
                         long owner, OwnerType ownerType,
                         String name,
                         long createTime,
                         long updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.owner = owner;
        this.ownerType = ownerType;
        this.name = name;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public long getOwner() {
        return owner;
    }

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public String getName() {
        return name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public static final class Builder {
        private Long id;
        private Long parentId;
        private long owner;
        private OwnerType ownerType;
        private String name;
        private long createTime;
        private long updateTime;

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

        public Builder setOwnerType(OwnerType ownerType) {
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

        public UserDirectory build() {
            return new UserDirectory(id, parentId, owner, ownerType,
                    name, createTime, updateTime);
        }
    }

}
