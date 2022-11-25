package org.huel.cloudhub.client.data.database.dao;

import org.huel.cloudhub.client.data.entity.object.VersionedObject;
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
public abstract class VersionedObjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(VersionedObject... versionedObjects);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<VersionedObject> versionedObjects);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(VersionedObject... versionedObjects);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<VersionedObject> versionedObjects);

    @Delete
    public abstract void delete(VersionedObject... versionedObjects);

    @Delete
    public abstract void delete(List<VersionedObject> versionedObjects);

    @Delete("DELETE FROM object_version_table WHERE bucket_name = {bucketName} AND object_name = {objectName} AND object_version = {version}")
    public abstract void deleteByVersion(String bucketName, String objectName, long version);

    @Query("SELECT * FROM object_version_table WHERE bucket_name = {bucketName} AND object_name = {objectName} ORDER BY object_version DESC LIMIT 1")
    public abstract VersionedObject getLatestObjectVersion(String bucketName, String objectName);

    @Query("SELECT * FROM object_version_table WHERE bucket_name = {bucketName} AND object_name = {objectName} ORDER BY object_version DESC")
    public abstract List<VersionedObject> getObjectsVersion(String bucketName, String objectName);

    @Query("SELECT * FROM object_version_table WHERE bucket_name = {bucketName} AND object_name = {objectName} AND object_version = {version}")
    public abstract VersionedObject getObjectVersion(String bucketName, String objectName, long version);

}
