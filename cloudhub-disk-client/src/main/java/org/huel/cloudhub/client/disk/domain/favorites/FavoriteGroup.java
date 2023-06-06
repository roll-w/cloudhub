package org.huel.cloudhub.client.disk.domain.favorites;

import org.huel.cloudhub.client.disk.database.DataItem;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
@DataTable(name = "favorite_group")
public class FavoriteGroup implements DataItem {
    public static final FavoriteGroup SYSTEM_FAVORITE_GROUP = new FavoriteGroup(
            0L, "default", 0, true,
            0, 0, false
    );

    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "user_id")
    private final long userId;

    @DataColumn(name = "public")
    private final boolean isPublic;

    @DataColumn(name = "create_time")
    private final long createTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    @DataColumn(name = "deleted")
    private final boolean deleted;

    public FavoriteGroup(Long id, String name, long userId, boolean isPublic,
                         long createTime, long updateTime, boolean deleted) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.isPublic = isPublic;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.deleted = deleted;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isPublic() {
        return isPublic;
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

    public static class Builder {
        private Long id;
        private String name;
        private long userId;
        private boolean isPublic;
        private long createTime;
        private long updateTime;
        private boolean deleted;

        public Builder() {
        }

        public Builder(FavoriteGroup favoriteGroup) {
            this.id = favoriteGroup.id;
            this.name = favoriteGroup.name;
            this.userId = favoriteGroup.userId;
            this.isPublic = favoriteGroup.isPublic;
            this.createTime = favoriteGroup.createTime;
            this.updateTime = favoriteGroup.updateTime;
            this.deleted = favoriteGroup.deleted;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setPublic(boolean aPublic) {
            isPublic = aPublic;
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

        public FavoriteGroup build() {
            return new FavoriteGroup(id, name, userId, isPublic,
                    createTime, updateTime, deleted);
        }
    }
}
