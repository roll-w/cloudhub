package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.OwnerType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Delete;
import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;
import space.lingu.light.Query;
import space.lingu.light.Update;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class UserFileStorageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(UserFileStorage... userFileStorages);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(List<UserFileStorage> userFileStorages);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(UserFileStorage... userFileStorages);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(List<UserFileStorage> userFileStorages);

    @Delete
    public abstract void delete(UserFileStorage UserFileStorage);

    @Delete
    public abstract void delete(List<UserFileStorage> userFileStorages);

    @Delete("DELETE FROM user_file_storage")
    public abstract void clearTable();

    @Query("SELECT * FROM user_file_storage")
    public abstract List<UserFileStorage> get();

    @Query("SELECT * FROM user_file_storage LIMIT {offset.limit()} OFFSET {offset.offset()}")
    public abstract List<UserFileStorage> get(Offset offset);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType}")
    public abstract List<UserFileStorage> get(long owner, OwnerType ownerType);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType} AND directory_id = {directoryId}")
    public abstract List<UserFileStorage> getByDirectoryId(long directoryId, long owner, OwnerType ownerType);

    @Query("SELECT * FROM user_file_storage WHERE directory_id = {directoryId}")
    public abstract List<UserFileStorage> getByDirectoryId(long directoryId);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType} AND file_type = {fileType}")
    public abstract List<UserFileStorage> getByType(long owner, OwnerType ownerType, FileType fileType);

    @Query("SELECT * FROM user_file_storage WHERE id = {id}")
    public abstract UserFileStorage getById(long id);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType} AND directory_id = {directoryId} AND name = {name}")
    public abstract UserFileStorage getById(long owner, OwnerType ownerType, long directoryId, String name);
}
