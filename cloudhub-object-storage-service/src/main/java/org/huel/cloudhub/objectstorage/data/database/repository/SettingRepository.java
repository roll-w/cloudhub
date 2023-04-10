package org.huel.cloudhub.objectstorage.data.database.repository;

import org.huel.cloudhub.objectstorage.data.database.CloudhubDatabase;
import org.huel.cloudhub.objectstorage.data.database.dao.SettingDao;
import org.huel.cloudhub.objectstorage.data.entity.SettingItem;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class SettingRepository {
    // TODO: 设置

    private final SettingDao settingDao;

    public SettingRepository(CloudhubDatabase database) {
        settingDao = database.getSettingDao();
        retrieveAll();
    }

    private void retrieveAll() {
        settingDao.get();
    }


    @Cacheable(cacheNames = "setting", key = "#settingItem.key()")
    public SettingItem put(SettingItem settingItem) {
        return settingItem;
    }

    public SettingItem put(String key, String value) {
        return put(new SettingItem(key, value));
    }

    public SettingItem remove(String key, String value) {
        return remove(new SettingItem(key, value));
    }

    public SettingItem remove(SettingItem settingItem) {
        return settingItem;
    }
}
