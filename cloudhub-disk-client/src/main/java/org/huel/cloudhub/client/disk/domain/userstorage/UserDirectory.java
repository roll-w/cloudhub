package org.huel.cloudhub.client.disk.domain.userstorage;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;
import space.lingu.light.SQLDataType;

/**
 * 用户定义的文件夹/目录
 *
 * @author RollW
 */
@DataTable
public class UserDirectory {
    public static final long ROOT = 0;
    @DataColumn(name = "id")
    @PrimaryKey
    private final Long id;

    @DataColumn(name = "parent_id")
    private final Long parentId;

    @DataColumn(name = "user_id")
    private final long userId;

    @DataColumn(name = "name")
    private final String name;

    @DataColumn(name = "create_time", dataType = SQLDataType.TIMESTAMP)
    private final long createTime;

    @DataColumn(name = "update_time", dataType = SQLDataType.TIMESTAMP)
    private final long updateTime;

    public UserDirectory(Long id, Long parentId,
                         long userId, String name,
                         long createTime,
                         long updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.userId = userId;
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

    public long getUserId() {
        return userId;
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
}
