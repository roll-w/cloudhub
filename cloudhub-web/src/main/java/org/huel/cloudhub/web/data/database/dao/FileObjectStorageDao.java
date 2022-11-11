package org.huel.cloudhub.web.data.database.dao;

import org.huel.cloudhub.web.data.entity.object.FileObjectStorage;
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
    @Delete("DELETE file_object_storage_table WHERE file_id = {id}")
    public abstract void deleteByImageId(String id);

    @Query("SELECT * FROM file_object_storage_table WHERE object_name = {objectName} AND bucket_id = {bucketId}")
    public abstract FileObjectStorage getObject(String bucketId, String objectName);

    @Query("SELECT * FROM file_object_storage_table WHERE bucket_id = {bucketId}")
    public abstract List<FileObjectStorage> getObjectsByBucketId(String bucketId);
}
