package org.huel.cloudhub.client.data.database;

import org.huel.cloudhub.client.data.database.dao.*;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import org.huel.cloudhub.client.data.entity.SettingItem;
import org.huel.cloudhub.client.data.entity.token.RegisterVerificationToken;
import org.huel.cloudhub.client.data.entity.user.User;
import space.lingu.light.Database;
import space.lingu.light.LightConfiguration;
import space.lingu.light.LightDatabase;

/**
 * 数据库访问
 *
 * @author RollW
 */
@Database(name = "cloudhub_database", version = 1,
        tables = {User.class, Bucket.class,FileObjectStorage.class,
                RegisterVerificationToken.class, SettingItem.class},
        configuration = @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "255"))
public abstract class CloudhubDatabase extends LightDatabase {
    public abstract FileObjectStorageDao getFileObjectStorageDao();

    public abstract UserDao getUserDao();

    public abstract VerificationTokenDao getVerificationTokenDao();

    public abstract SettingDao getSettingDao();

    public abstract BucketDao getBucketDao();
}
