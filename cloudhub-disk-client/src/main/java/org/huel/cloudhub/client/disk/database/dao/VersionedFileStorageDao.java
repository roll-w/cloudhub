package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface VersionedFileStorageDao extends AutoPrimaryBaseDao<VersionedFileStorage> {

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} ORDER BY version DESC LIMIT 1")
    VersionedFileStorage getLatestFileVersion(long storageId);

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} AND deleted = 0 ORDER BY version DESC")
    List<VersionedFileStorage> getFileVersions(long storageId);

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} ORDER BY version DESC")
    List<VersionedFileStorage> getFileVersionsIncludeDelete(long storageId);

    @Query("SELECT * FROM versioned_file_storage WHERE storage_id = {storageId} AND version = {version}")
    VersionedFileStorage getFileVersion(long storageId, long version);

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE deleted = 0")
    List<VersionedFileStorage> getActives();

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE deleted = 1")
    List<VersionedFileStorage> getInactives();

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE id = {id}")
    VersionedFileStorage getById(long id);

    @Override
    @Query("SELECT * FROM versioned_file_storage WHERE id IN {ids}")
    List<VersionedFileStorage> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM versioned_file_storage WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM versioned_file_storage WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM versioned_file_storage")
    List<VersionedFileStorage> get();

    @Override
    @Query("SELECT COUNT(*) FROM versioned_file_storage")
    int count();

    @Override
    @Query("SELECT * FROM versioned_file_storage LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<VersionedFileStorage> get(Offset offset);

    @Override
    default String getTableName() {
        return "versioned_file_storage";
    }
}
