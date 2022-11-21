package org.huel.cloudhub.client.data.database.dao;

import org.huel.cloudhub.client.data.entity.object.ObjectMetadata;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class ObjectMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ObjectMetadata... metadata);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<ObjectMetadata> metadata);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(ObjectMetadata... metadata);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<ObjectMetadata> metadata);

    @Delete
    public abstract void delete(ObjectMetadata... metadata);

    @Delete
    public abstract void delete(List<ObjectMetadata> metadata);

    @Delete("DELETE object_metadata_table WHERE object_name = {objectName} AND bucket_name = {bucketName}")
    public abstract void deleteByObjectName(String bucketName, String objectName);

    @Query("SELECT * FROM object_metadata_table WHERE object_name = {objectName} AND bucket_name = {bucketName}")
    public abstract ObjectMetadata getByObjectName(String bucketName, String objectName);

}
