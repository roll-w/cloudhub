package org.huel.cloudhub.client.data.entity.bucket;

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
     * 不能包含相同的桶名称（唯一ID）
     */
    @PrimaryKey
    @DataColumn(name = "bucket_name")
    private String name;

    // 此桶所从属于的用户ID
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
