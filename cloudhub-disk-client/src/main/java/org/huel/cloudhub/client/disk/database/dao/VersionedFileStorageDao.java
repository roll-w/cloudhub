package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;
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

    @Query("SELECT * FROM versioned_file_storage WHERE id = {id}")
    VersionedFileStorage getById(long id);
}
