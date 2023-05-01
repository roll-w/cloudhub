package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.storage.DiskFileStorage;
import org.huel.cloudhub.client.disk.domain.storage.dto.StorageAsSize;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Delete;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface DiskFileStorageDao extends BaseDao<DiskFileStorage> {
    @Delete("DELETE FROM disk_file_storage")
    void clearTable();

    @Query("SELECT * FROM disk_file_storage")
    List<DiskFileStorage> get();

    @Query("SELECT * FROM disk_file_storage LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<DiskFileStorage> get(Offset offset);

    @Query("SELECT * FROM disk_file_storage WHERE file_id = {fileId}")
    DiskFileStorage getById(String fileId);

    @Query("SELECT `file_id`, `size` FROM disk_file_storage WHERE file_id IN {fileIds}")
    List<StorageAsSize> getSizesByIds(List<String> fileIds);

    @Query("SELECT `size` FROM disk_file_storage WHERE file_id = {fileId}")
    long getSizeById(String fileId);
}
