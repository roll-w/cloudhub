package org.huel.cloudhub.web.data.entity;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

/**
 * 用户从属用户组设置
 *
 * @author RollW
 */
@DataTable(tableName = "grouped_user_table")
public class GroupedUser {
    @DataColumn(name = "group_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
    @PrimaryKey
    private String userGroup;

    @DataColumn(name = "user_id")
    @PrimaryKey
    private Long userId;

    public GroupedUser() {
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
