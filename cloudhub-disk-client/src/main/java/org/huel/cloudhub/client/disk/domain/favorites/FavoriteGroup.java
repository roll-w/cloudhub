package org.huel.cloudhub.client.disk.domain.favorites;

import org.huel.cloudhub.client.disk.database.DataItem;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * @author RollW
 */
@DataTable(name = "favorite_group")
public class FavoriteGroup implements DataItem {
    public static final FavoriteGroup SYSTEM_FAVORITE_GROUP = new FavoriteGroup(
            0L, "default", 0L, true,
            0L, 0L, false
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

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
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
}
