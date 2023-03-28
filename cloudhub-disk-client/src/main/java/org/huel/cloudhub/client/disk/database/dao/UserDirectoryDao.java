package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.userstorage.OwnerType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;
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
public abstract class UserDirectoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(UserDirectory... userDirectories);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(List<UserDirectory> userDirectories);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(UserDirectory... userDirectories);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(List<UserDirectory> userDirectories);

    @Delete
    public abstract void delete(UserDirectory UserDirectory);

    @Delete
    public abstract void delete(List<UserDirectory> userDirectories);

    @Delete("DELETE FROM user_directory")
    public abstract void clearTable();

    @Query("SELECT * FROM user_directory")
    public abstract List<UserDirectory> get();

    @Query("SELECT * FROM user_directory LIMIT {offset.limit()} OFFSET {offset.offset()}")
    public abstract List<UserDirectory> get(Offset offset);

    @Query("SELECT * FROM user_directory WHERE id = {id}")
    public abstract UserDirectory getById(long id);

    @Query("SELECT * FROM user_directory WHERE parent_id = {parentId}")
    public abstract List<UserDirectory> getByParentId(long parentId);

    @Query("SELECT * FROM user_directory WHERE parent_id = {parentId} LIMIT {offset.limit()} OFFSET {offset.offset()}")
    public abstract List<UserDirectory> getByParentId(long parentId, Offset offset);

    @Query("SELECT * FROM user_directory WHERE parent_id = {parentId} AND owner = {owner} AND owner_type = {ownerType}")
    public abstract List<UserDirectory> getByParentId(long parentId,
                                                      long owner,
                                                      OwnerType ownerType);

    @Query("SELECT * FROM user_directory WHERE name = {name} AND parent_id = {parentId} AND owner = {owner} AND owner_type = {ownerType}")
    public abstract UserDirectory getByName(String name, long parentId, long owner, OwnerType ownerType);
}
