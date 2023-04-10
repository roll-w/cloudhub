package org.huel.cloudhub.objectstorage.data.database.dao;

import org.huel.cloudhub.objectstorage.data.entity.object.FileReference;
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
public abstract class FileReferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FileReference... fileReferences);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<FileReference> fileReferences);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(FileReference... fileReferences);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<FileReference> fileReferences);

    @Delete
    public abstract void delete(FileReference... fileReferences);

    @Delete
    public abstract void delete(List<FileReference> fileReferences);

    @Query("SELECT * FROM file_reference_table WHERE file_id = {fileId}")
    public abstract FileReference getReference(String fileId);
}
