package org.huel.cloudhub.web.data.database.dao;

import org.huel.cloudhub.web.data.entity.FileObjectStorage;
import org.huel.cloudhub.web.data.entity.UserUploadFileStorage;
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


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertUserUploadFileStorage(UserUploadFileStorage... storages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertUserUploadFileStorage(List<UserUploadFileStorage> storages);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateUserUploadFileStorage(UserUploadFileStorage... storages);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateUserUploadFileStorage(List<UserUploadFileStorage> storages);

    @Delete
    public abstract void deleteUserUploadFileStorage(UserUploadFileStorage... storages);

    @Delete
    public abstract void deleteUserUploadFileStorage(List<UserUploadFileStorage> storages);


    @Transaction
    @Delete("DELETE file_object_storage_table WHERE image_id = {id}")
    public abstract void deleteByImageId(String id);

    @Query("SELECT * FROM file_object_storage_table WHERE image_id = {id}")
    public abstract FileObjectStorage getByImageId(String id);

    @Query("SELECT * FROM file_object_storage_table WHERE user_id = {userId}")
    public abstract List<UserUploadFileStorage> getUploadsByUserId(long userId);

    @Query("SELECT * FROM file_object_storage_table WHERE user_id = {userId} AND image_id = {imageId}")
    public abstract UserUploadFileStorage getUploadByUserAndImageId(long userId, String imageId);

}
