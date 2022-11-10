package org.huel.cloudhub.web.data.database;

import org.huel.cloudhub.web.data.database.dao.*;
import org.huel.cloudhub.web.data.entity.*;
import space.lingu.light.Database;
import space.lingu.light.LightConfiguration;
import space.lingu.light.LightDatabase;

/**
 * 数据库访问
 *
 * @author RollW
 */
@Database(name = "cloudhub_database", version = 1,
        tables = {User.class,
                //FileObjectStorage.class, UserUploadFileStorage.class,
                RegisterVerificationToken.class,
                UserGroupConfig.class, GroupedUser.class,
                SettingItem.class},
        configuration = @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "255"))
public abstract class CloudhubDatabase extends LightDatabase {
    public abstract FileObjectStorageDao getFileObjectStorageDao();

    public abstract UserDao getUserDao();

    public abstract VerificationTokenDao getVerificationTokenDao();

    public abstract UserGroupDao getUserGroupConfigDao();

    public abstract SettingDao getSettingDao();
}
