package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupMember;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserGroupMemberDao extends AutoPrimaryBaseDao<UserGroupMember> {
    @Override
    @Query("SELECT * FROM user_group_member WHERE deleted = 0")
    List<UserGroupMember> getActives();

    @Override
    @Query("SELECT * FROM user_group_member WHERE deleted = 1")
    List<UserGroupMember> getInactives();

    @Override
    @Query("SELECT * FROM user_group_member WHERE id = {id}")
    UserGroupMember getById(long id);

    @Override
    @Query("SELECT * FROM user_group_member WHERE id IN ({ids})")
    List<UserGroupMember> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_group_member WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_group_member WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_group_member ORDER BY id DESC")
    List<UserGroupMember> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_group_member")
    int count();

    @Override
    @Query("SELECT * FROM user_group_member ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserGroupMember> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_group_member";
    }
}
