package org.huel.cloudhub.objectstorage.data.database.dao;

import org.huel.cloudhub.objectstorage.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.objectstorage.data.entity.bucket.Bucket;
import space.lingu.light.*;

import java.util.List;

/**
 * @author Cheng
 * <p>
 * 桶的name是主键唯一
 */
@Dao
public abstract class BucketDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(Bucket... buckets);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(List<Bucket> buckets);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(Bucket... buckets);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(List<Bucket> buckets);

    @Delete
    public abstract void delete(Bucket... buckets);

    @Transaction
    @Delete("DELETE FROM user_buckets_table WHERE bucket_name = {info.name()}")
    public abstract void delete(BucketInfo info);

    @Delete("DELETE FROM user_buckets_table WHERE bucket_name = {name}")
    public abstract void deleteByName(String name);

    @Delete
    public abstract void delete(List<Bucket> buckets);

    // 包含桶的名称和用户id的集合
    @Query("SELECT * FROM user_buckets_table")
    public abstract List<BucketInfo> bucketInfos();

    //    根据名称查询一个桶对象
    @Query("SELECT * FROM user_buckets_table WHERE bucket_name = {name}")
    public abstract Bucket getBucketByName(String name);

    //  根据用户id查询出桶的集合
    @Query("SELECT * FROM user_buckets_table WHERE bucket_user_id = {id}")
    public abstract List<Bucket> getBucketsByUserId(long id);

    @Query("SELECT * FROM user_buckets_table WHERE bucket_user_id = {id}")
    public abstract List<BucketInfo> getBucketInfosByUserId(long id);

    @Query("SELECT * FROM user_buckets_table")
    public abstract List<BucketInfo> getBucketInfos();

    @Query("SELECT COUNT(*) FROM user_buckets_table")
    public abstract int getBucketCount();

    //  根据用户id查询出桶的名称集合
    @Query("SELECT bucket_name FROM user_buckets_table WHERE bucket_user_id = {id}")
    public abstract List<String> getBucketNamesByUserId(long id);

    //  根据桶的名称查询桶的名字 -.-
    @Query("SELECT bucket_name FROM user_buckets_table WHERE bucket_name = {name}")
    public abstract String getBucketNameByName(String name);

}
