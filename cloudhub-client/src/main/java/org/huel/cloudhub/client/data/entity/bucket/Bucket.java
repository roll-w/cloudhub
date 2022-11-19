package org.huel.cloudhub.client.data.entity.bucket;

import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

/**
 * 存储桶（文件的容器）
 * 需要实现桶的增删改查
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

    @DataColumn(name = "bucket_create_time")
    private long createTime;

    @DataColumn(name = "bucket_visibility", configuration =
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "20"))
    private BucketVisibility bucketVisibility;

    public Bucket(String name, Long userId,
                  long createTime, BucketVisibility bucketVisibility) {
        this.name = name;
        this.userId = userId;
        this.createTime = createTime;
        this.bucketVisibility = bucketVisibility;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public BucketVisibility getBucketVisibility() {
        return bucketVisibility;
    }

    public void setBucketVisibility(BucketVisibility bucketVisibility) {
        this.bucketVisibility = bucketVisibility;
    }

    public BucketInfo toInfo() {
        return new BucketInfo(name, userId, createTime, bucketVisibility);
    }
}
