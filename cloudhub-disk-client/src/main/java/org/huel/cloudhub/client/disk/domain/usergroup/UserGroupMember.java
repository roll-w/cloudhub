package org.huel.cloudhub.client.disk.domain.usergroup;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "user_group_member")
public class UserGroupMember implements DataItem {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "group_id")
    private final long groupId;

    @DataColumn(name = "user_id")
    private final long userId;

    @DataColumn(name = "user_type")
    private final LegalUserType userType;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;


    public UserGroupMember(Long id, long groupId, long userId,
                           LegalUserType userType,
                           long createTime, long updateTime,
                           boolean deleted) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.userType = userType;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getGroupId() {
        return groupId;
    }

    public long getUserId() {
        return userId;
    }

    public LegalUserType getUserType() {
        return userType;
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
        private long groupId;
        private long userId;
        private LegalUserType userType;
        private long createTime;
        private long updateTime;
        private boolean deleted;

        private Builder() {
        }

        private Builder(UserGroupMember usergroupmember) {
            this.id = usergroupmember.id;
            this.groupId = usergroupmember.groupId;
            this.userId = usergroupmember.userId;
            this.userType = usergroupmember.userType;
            this.createTime = usergroupmember.createTime;
            this.updateTime = usergroupmember.updateTime;
            this.deleted = usergroupmember.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setGroupId(long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUserType(LegalUserType userType) {
            this.userType = userType;
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

        public UserGroupMember build() {
            return new UserGroupMember(
                    id, groupId, userId, userType,
                    createTime, updateTime, deleted
            );
        }
    }
}
