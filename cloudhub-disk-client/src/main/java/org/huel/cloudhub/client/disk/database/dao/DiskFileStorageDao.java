package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.storage.DiskFileStorage;
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
public abstract class DiskFileStorageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(DiskFileStorage... diskFileStorages);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(List<DiskFileStorage> diskFileStorages);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(DiskFileStorage... diskFileStorages);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(List<DiskFileStorage> diskFileStorages);

    @Delete
    public abstract void delete(DiskFileStorage DiskFileStorage);

    @Delete
    public abstract void delete(List<DiskFileStorage> diskFileStorages);

    @Delete("DELETE FROM disk_file_storage")
    public abstract void clearTable();

    @Query("SELECT * FROM disk_file_storage")
    public abstract List<DiskFileStorage> get();

    @Query("SELECT * FROM disk_file_storage LIMIT {offset.limit()} OFFSET {offset.offset()}")
    public abstract List<DiskFileStorage> get(Offset offset);

    @Query("SELECT * FROM disk_file_storage WHERE file_id = {fileId}")
    public abstract DiskFileStorage getById(String fileId);
}
