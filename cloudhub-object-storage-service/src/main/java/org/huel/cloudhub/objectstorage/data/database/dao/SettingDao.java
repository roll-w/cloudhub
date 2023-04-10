package org.huel.cloudhub.objectstorage.data.database.dao;

import org.huel.cloudhub.objectstorage.data.entity.SettingItem;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(SettingItem items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<SettingItem> items);

    @Query("SELECT * FROM setting_table")
    public abstract List<SettingItem> get();

    @Query("SELECT * FROM setting_table WHERE `key` = {key}")
    public abstract SettingItem get(String key);

    @Query("SELECT * FROM setting_table WHERE `key` IN {keys}")
    public abstract List<SettingItem> get(List<String> keys);

    @Update
    public abstract void update(SettingItem items);

    @Update
    public abstract void update(List<SettingItem> items);

    @Delete
    public abstract void delete(SettingItem items);

    @Delete
    public abstract void delete(List<SettingItem> items);

    @Delete("DELETE FROM setting_table")
    public abstract void delete();

    @Delete("DELETE FROM setting_table WHERE `key` = {key}")
    public abstract void delete(String key);
}
