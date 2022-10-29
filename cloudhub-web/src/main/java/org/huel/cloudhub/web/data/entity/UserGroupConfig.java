package org.huel.cloudhub.web.data.entity;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

/**
 * 用户分组配置
 *
 * @author RollW
 */
@DataTable(tableName = "user_group_config_table")
public class UserGroupConfig {
    public static final UserGroupConfig DEFAULT = new UserGroupConfig(
            "default", "Default user group config",
            500, 500);

    @DataColumn(name = "group_name", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
    @PrimaryKey
    private String name;// 名称 - 主键

    @DataColumn(name = "group_description", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "256"))
    private String description;// TODO:

    /**
     * 最大文件总限量。以兆为单位。
     */
    @DataColumn(name = "group_max_file")
    private int maxFileSize;

    /**
     * 最大上传文件数量。
     */
    @DataColumn(name = "group_max_num")
    private int maxNum;

    public UserGroupConfig() {
    }

    public UserGroupConfig(String name, String description, int maxFileSize, int maxNum) {
        this.name = name;
        this.description = description;
        this.maxFileSize = maxFileSize;
        this.maxNum = maxNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
