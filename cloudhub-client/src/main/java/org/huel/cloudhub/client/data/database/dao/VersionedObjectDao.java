package org.huel.cloudhub.client.data.database.dao;

import org.huel.cloudhub.client.data.entity.object.VersionedObject;
import space.lingu.light.Dao;
import space.lingu.light.Delete;
import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;
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
}
