package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserDirectoryDao extends AutoPrimaryBaseDao<UserDirectory> {

    @Query("SELECT * FROM user_directory WHERE parent_id = {parentId}")
    List<UserDirectory> getByParentId(long parentId);

    @Query("SELECT * FROM user_directory WHERE parent_id = {parentId} LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserDirectory> getByParentId(long parentId, Offset offset);

    @Query("SELECT * FROM user_directory WHERE parent_id = {parentId} AND owner = {owner} AND owner_type = {ownerType}")
    List<UserDirectory> getByParentId(long parentId,
                                      long owner,
                                      LegalUserType ownerType);

    @Query("SELECT * FROM user_directory WHERE name = {name} AND parent_id = {parentId} AND owner = {owner} AND owner_type = {ownerType}")
    UserDirectory getByName(String name, long parentId, long owner, LegalUserType ownerType);

    @Override
    @Query("SELECT * FROM user_directory WHERE deleted = 0")
    List<UserDirectory> getActives();

    @Override
    @Query("SELECT * FROM user_directory WHERE deleted = 1")
    List<UserDirectory> getInactives();

    @Override
    @Query("SELECT * FROM user_directory WHERE id = {id}")
    UserDirectory getById(long id);

    @Override
    @Query("SELECT * FROM user_directory WHERE id IN {ids}")
    List<UserDirectory> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_directory WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_directory WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_directory")
    List<UserDirectory> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_directory")
    int count();

    @Override
    @Query("SELECT * FROM user_directory LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserDirectory> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_directory";
    }

    @Query("SELECT * FROM user_directory WHERE id = {directoryId} AND owner = {ownerId} AND owner_type = {ownerType}")
    UserDirectory getById(long directoryId, long ownerId, LegalUserType ownerType);
}
