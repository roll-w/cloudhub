package org.huel.cloudhub.web.data.database.dao;

import org.huel.cloudhub.web.data.entity.GroupedUser;
import org.huel.cloudhub.web.data.entity.UserGroupConfig;
import space.lingu.NonNull;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class UserGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(UserGroupConfig... configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(UserGroupConfig config);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<UserGroupConfig> configs);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(UserGroupConfig... configs);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<UserGroupConfig> configs);

    @Delete
    public abstract void delete(UserGroupConfig... configs);

    @Delete
    public abstract void delete(List<UserGroupConfig> configs);

    @Query("SELECT * FROM user_group_config_table WHERE group_name = {name}")
    public abstract UserGroupConfig getGroupConfigByName(String name);

    @NonNull
    public UserGroupConfig getGroupConfigByNameOrDefault(String name) {
        UserGroupConfig config = getGroupConfigByName(name);
        if (config == null) {
            return UserGroupConfig.DEFAULT;
        }
        return config;
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertGroupedUser(GroupedUser... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertGroupedUser(GroupedUser user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertGroupedUser(List<GroupedUser> users);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateGroupedUser(GroupedUser... users);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateGroupedUser(List<GroupedUser> users);

    @Delete
    public abstract void deleteGroupedUser(GroupedUser... users);

    @Delete
    public abstract void deleteGroupedUser(List<GroupedUser> users);

    @Query("SELECT * FROM grouped_user_table WHERE group_name = {name}")
    public abstract List<GroupedUser> getGroupedUsersByName(String name);

    @Query("SELECT * FROM grouped_user_table WHERE user_id = {id}")
    public abstract GroupedUser getGroupedUserById(long id);


}
