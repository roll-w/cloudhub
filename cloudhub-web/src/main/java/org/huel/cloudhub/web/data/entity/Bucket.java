package org.huel.cloudhub.web.data.entity;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

/**
 * 存储桶（文件的容器）
 *
 * @author RollW
 */
// TODO:
@DataTable(tableName = "user_buckets_table", configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public class Bucket {
    /**
     * 桶名称由用户指定。
     * <p>
     * 同一用户下不能包含相同的桶名称
     */
    @PrimaryKey
    @DataColumn(name = "bucket_name")
    private String name;
    // 最终访问时需要再附上用户ID。
    // 格式类似于: 用户ID-桶名称。
    @PrimaryKey
    @DataColumn(name = "bucket_user_id")
    private Long userId;

    public Bucket(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }

    public Bucket() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String toBucketId() {
        if (userId == null || name == null) {
            throw new NullPointerException();
        }
        return userId + "-" + name;
    }
}
