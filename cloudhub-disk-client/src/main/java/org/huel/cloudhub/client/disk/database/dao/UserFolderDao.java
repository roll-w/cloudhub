package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.NonNull;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserFolderDao extends AutoPrimaryBaseDao<UserFolder> {

    @Query("SELECT * FROM user_folder WHERE parent_id = {parentId}")
    List<UserFolder> getByParentId(long parentId);

    @Query("SELECT * FROM user_folder WHERE parent_id = {parentId} LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserFolder> getByParentId(long parentId, Offset offset);

    @Query("SELECT * FROM user_folder WHERE parent_id = {parentId} AND owner = {owner} AND owner_type = {ownerType}")
    List<UserFolder> getByParentId(long parentId,
                                   long owner,
                                   LegalUserType ownerType);

    @Query("SELECT * FROM user_folder WHERE name = {name} AND parent_id = {parentId} AND owner = {owner} AND owner_type = {ownerType}")
    UserFolder getByName(String name, long parentId, long owner, LegalUserType ownerType);

    @Query("SELECT * FROM user_folder WHERE name = {name} AND parent_id = {parentId} ")
    UserFolder getByName(String name, long parentId);

    @Override
    @Query("SELECT * FROM user_folder WHERE deleted = 0")
    List<UserFolder> getActives();

    @Override
    @Query("SELECT * FROM user_folder WHERE deleted = 1")
    List<UserFolder> getInactives();

    @Override
    @Query("SELECT * FROM user_folder WHERE id = {id}")
    UserFolder getById(long id);

    @Override
    @Query("SELECT * FROM user_folder WHERE id IN ({ids})")
    List<UserFolder> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_folder WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_folder WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_folder")
    List<UserFolder> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_folder")
    int count();

    @Override
    @Query("SELECT * FROM user_folder LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserFolder> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_folder";
    }

    @Query("SELECT * FROM user_folder WHERE id = {directoryId} AND owner = {ownerId} AND owner_type = {ownerType}")
    UserFolder getById(long directoryId, long ownerId, LegalUserType ownerType);

    @Query("""
            SELECT f2.id
            FROM (SELECT @r                                                  AS _id,
                         (SELECT @r := parent_id FROM user_folder WHERE id = _id) AS parent_id,
                         @l := @l + 1                                        AS lv
                  FROM (SELECT @r := {folderId}, @l := 0) vars,
                       user_folder h
                  WHERE @r <> 0) f1
                     JOIN user_folder f2
                          ON f1._id = f2.id
            ORDER BY f1.lv DESC;
            """)
    List<Long> getParentFolderIds(long folderId);

    @Query("SELECT * FROM user_folder WHERE name LIKE CONCAT('%', {name}, '%') " +
            "AND owner = {storageOwner.getOwnerId()} " +
            "AND owner_type = {storageOwner.getOwnerType()}")
    List<UserFolder> findFoldersLike(String name, StorageOwner storageOwner);

    @Query("SELECT * FROM user_folder WHERE name LIKE CONCAT('%', {name}, '%') " +
            "AND owner = {storageOwner.getOwnerId()} " +
            "AND owner_type = {storageOwner.getOwnerType()} " +
            "AND parent_id = {parentId} " +
            "AND deleted = 0")
    List<UserFolder> findFoldersLike(String name, StorageOwner storageOwner, long parentId);

    @Query("SELECT * FROM user_folder WHERE name LIKE CONCAT('%', {name}, '%') " +
            "AND owner = {storageOwner.getOwnerId()} " +
            "AND owner_type = {storageOwner.getOwnerType()} " +
            "AND create_time BETWEEN {after} AND {before} " +
            "AND deleted = 0")
    List<UserFolder> findFoldersLikeAndBetween(String name, StorageOwner storageOwner,
                                               long before, long after);

    @Query("SELECT * FROM user_folder WHERE " +
            "owner = {storageOwner.getOwnerId()} " +
            "AND owner_type = {storageOwner.getOwnerType()} " +
            "AND create_time BETWEEN {after} AND {before} " +
            "AND deleted = 0")
    List<UserFolder> findFoldersBetween(StorageOwner storageOwner,
                                        long before, long after);

    default List<UserFolder> findFolders(StorageOwner storageOwner,
                                         @NonNull String name,
                                         Long before, Long after) {
        if (name.isEmpty()) {
            return findFoldersBetween(storageOwner, before, after);
        }
        if (before == null && after == null) {
            return findFoldersLike(name, storageOwner);
        }
        return findFoldersLikeAndBetween(name, storageOwner, before, after);
    }

    default List<UserFolder> findFoldersByCondition(StorageOwner storageOwner,
                                                    String name,
                                                    Long before, Long after) {
        if (name == null && before == null && after == null) {
            return List.of();
        }
        if (name == null) {
            name = "";
        }
        if (before == null) {
            before = Long.MAX_VALUE;
        }
        if (after == null) {
            after = -1L;
        }
        return findFolders(storageOwner, name, before, after);
    }
}
