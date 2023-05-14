package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserFileStorageDao extends AutoPrimaryBaseDao<UserFileStorage> {

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType}")
    List<UserFileStorage> get(long owner, LegalUserType ownerType);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType} AND directory_id = {directoryId}")
    List<UserFileStorage> getByDirectoryId(long directoryId, long owner, LegalUserType ownerType);

    @Query("SELECT * FROM user_file_storage WHERE directory_id = {directoryId}")
    List<UserFileStorage> getByDirectoryId(long directoryId);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType} AND file_category = {fileType} AND deleted = 0")
    List<UserFileStorage> getByType(long owner, LegalUserType ownerType, FileType fileType);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType} AND directory_id = {directoryId} AND name = {name}")
    UserFileStorage getById(long owner, LegalUserType ownerType, long directoryId, String name);

    @Query("SELECT * FROM user_file_storage WHERE id IN {storageIds} AND owner = {storageOwner.getOwnerId()} AND owner_type = {storageOwner.getOwnerType()}")
    List<UserFileStorage> getByIds(List<Long> storageIds, StorageOwner storageOwner);

    @Query("SELECT * FROM user_file_storage WHERE id IN {storageIds} AND file_category = {fileType}")
    List<UserFileStorage> getByIdsAndType(List<Long> storageIds, FileType fileType);

    @Query("SELECT * FROM user_file_storage WHERE id IN {storageIds} AND file_category = {fileType} AND owner = {storageOwner.getOwnerId()} AND owner_type = {storageOwner.getOwnerType()}")
    List<UserFileStorage> getByIdsAndType(List<Long> storageIds, FileType fileType, StorageOwner storageOwner);

    @Query("SELECT * FROM user_file_storage WHERE owner = {owner} AND owner_type = {ownerType} AND deleted = 1")
    List<UserFileStorage> getDeletedByOwner(long owner, LegalUserType ownerType);

    @Override
    @Query("SELECT * FROM user_file_storage WHERE deleted = 0")
    List<UserFileStorage> getActives();

    @Override
    @Query("SELECT * FROM user_file_storage WHERE deleted = 1")
    List<UserFileStorage> getInactives();

    @Override
    @Query("SELECT * FROM user_file_storage WHERE id = {id}")
    UserFileStorage getById(long id);

    @Override
    @Query("SELECT * FROM user_file_storage WHERE id IN {ids}")
    List<UserFileStorage> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_file_storage WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_file_storage WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_file_storage")
    List<UserFileStorage> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_file_storage")
    int count();

    @Override
    @Query("SELECT * FROM user_file_storage LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserFileStorage> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_file_storage";
    }

    @Query("SELECT * FROM user_file_storage WHERE name LIKE {name} AND owner = {owner} AND owner_type = {legalUserType} AND deleted = 0")
    List<UserFileStorage> getFilesLike(String name, long owner, LegalUserType legalUserType);

    @Query("SELECT * FROM user_file_storage WHERE id = {fileId} AND owner = {ownerId} AND owner_type = {ownerType}")
    UserFileStorage getById(long fileId, long ownerId, LegalUserType ownerType);
}
