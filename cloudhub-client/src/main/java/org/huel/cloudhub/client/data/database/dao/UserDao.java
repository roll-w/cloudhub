package org.huel.cloudhub.client.data.database.dao;

import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.user.Role;
import org.huel.cloudhub.client.data.entity.user.User;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(User... users);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract long insert(User user);// with user id

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(List<User> users);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(User... users);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public abstract void update(List<User> users);

    @Delete
    public abstract void delete(User... users);

    @Transaction
    @Delete("DELETE FROM user_table WHERE user_id = {info.id()}")
    public abstract void delete(UserInfo info);

    @Delete("DELETE FROM user_table WHERE user_id = {id}")
    public abstract void deleteById(long id);

    @Delete("DELETE FROM user_table WHERE user_id IN {ids}")
    public abstract void deleteByIds(List<Long> ids);

    @Delete
    public abstract void delete(List<User> users);

    @Query("SELECT user_id, user_name, user_email, user_role FROM user_table")
    public abstract List<UserInfo> userInfos();

    @Query("SELECT * FROM user_table WHERE user_id = {id}")
    public abstract User getUserById(long id);

    @Query("SELECT * FROM user_table WHERE user_name = {name}")
    public abstract User getUserByName(String name);

    @Query("SELECT * FROM user_table WHERE user_email = {email}")
    public abstract User getUserByEmail(String email);

    @Query("SELECT user_name FROM user_table WHERE user_email = {email}")
    public abstract String getUsernameByEmail(String email);

    @Query("SELECT user_name FROM user_table WHERE user_id = {id}")
    public abstract String getUsernameById(long id);

    // 检查根据用户名查到的用户名
    // 如果SELECT * 会导致查询的量增大，影响速度
    @Query("SELECT user_name FROM user_table WHERE user_name = {name}")
    public abstract String getUsernameByName(String name);

    //登录查询用户的角色信息进行授权
    @Query("SELECT user_role FROM user_table WHERE user_id = {id}")
    public abstract Role getRoleByName(long id);

}
