package org.huel.cloudhub.meta.server.data.database.dao;

import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class FileStorageLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FileStorageLocation... fileStorageLocations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FileStorageLocation fileStorageLocation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<FileStorageLocation> fileStorageLocations);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(FileStorageLocation... fileStorageLocations);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<FileStorageLocation> fileStorageLocations);

    @Delete
    public abstract void delete(FileStorageLocation... fileStorageLocations);


    @Delete("DELETE FROM file_storage_location_table WHERE file_id = {fileId}")
    public abstract void deleteById(String fileId);

    @Query("SELECT * FROM file_storage_location_table WHERE file_id = {fileId}")
    public abstract FileStorageLocation getByFileId(String fileId);

    @Query("SELECT file_id FROM file_storage_location_table WHERE file_id = {fileId}")
    public abstract String checkByFileId(String fileId);
}
