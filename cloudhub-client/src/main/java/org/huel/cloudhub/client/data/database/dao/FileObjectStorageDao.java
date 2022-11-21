package org.huel.cloudhub.client.data.database.dao;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class FileObjectStorageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FileObjectStorage... storages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<FileObjectStorage> storages);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(FileObjectStorage... storages);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<FileObjectStorage> storages);

    @Delete
    public abstract void delete(FileObjectStorage... storages);

    @Delete
    public abstract void delete(List<FileObjectStorage> storages);

    @Transaction
    @Delete("DELETE FROM file_object_storage_table WHERE object_name = {objectName} AND bucket_name = {bucketName}")
    public abstract void deleteByObjectName(String bucketName, String objectName);

    @Delete("DELETE FROM file_object_storage_table WHERE bucket_name = {bucketName}")
    public abstract void deleteByBucketName(String bucketName);

    @Delete("DELETE FROM file_object_storage_table WHERE bucket_name = {bucketName} AND object_name in {objectNames}")
    public abstract void deleteByBucketNameWith(String bucketName, List<String> objectNames);

    @Query("SELECT * FROM file_object_storage_table WHERE object_name = {objectName} AND bucket_name = {bucketId}")
    public abstract FileObjectStorage getObject(String bucketId, String objectName);

    @Query("SELECT * FROM file_object_storage_table WHERE bucket_name = {bucketId}")
    public abstract List<FileObjectStorage> getObjectsByBucketName(String bucketId);

    @Query("SELECT bucket_name, object_name FROM file_object_storage_table WHERE bucket_name = {bucketId}")
    public abstract List<ObjectInfo> getObjectInfosByBucketName(String bucketId);

}
