package org.huel.cloudhub.objectstorage.data.database;

import org.huel.cloudhub.objectstorage.data.entity.bucket.Bucket;
import org.huel.cloudhub.objectstorage.data.entity.object.FileObjectStorage;
import org.huel.cloudhub.objectstorage.data.entity.SettingItem;
import org.huel.cloudhub.objectstorage.data.entity.object.FileReference;
import org.huel.cloudhub.objectstorage.data.entity.object.ObjectMetadata;
import org.huel.cloudhub.objectstorage.data.entity.object.VersionedObject;
import org.huel.cloudhub.objectstorage.data.entity.token.RegisterVerificationToken;
import org.huel.cloudhub.objectstorage.data.entity.user.User;
import org.huel.cloudhub.objectstorage.data.database.dao.BucketDao;
import org.huel.cloudhub.objectstorage.data.database.dao.FileObjectStorageDao;
import org.huel.cloudhub.objectstorage.data.database.dao.FileReferenceDao;
import org.huel.cloudhub.objectstorage.data.database.dao.ObjectMetadataDao;
import org.huel.cloudhub.objectstorage.data.database.dao.SettingDao;
import org.huel.cloudhub.objectstorage.data.database.dao.UserDao;
import org.huel.cloudhub.objectstorage.data.database.dao.VerificationTokenDao;
import org.huel.cloudhub.objectstorage.data.database.dao.VersionedObjectDao;
import space.lingu.light.DataConverters;
import space.lingu.light.Database;
import space.lingu.light.LightConfiguration;
import space.lingu.light.LightDatabase;

/**
 * 数据库访问
 *
 * @author RollW
 */
@Database(name = "cloudhub_database", version = 1,
        tables = {User.class, Bucket.class,
                FileObjectStorage.class, FileReference.class,
                ObjectMetadata.class, VersionedObject.class,
                RegisterVerificationToken.class, SettingItem.class})
@DataConverters({CloudhubConverter.class})
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "255")
public abstract class CloudhubDatabase extends LightDatabase {
    public abstract FileObjectStorageDao getFileObjectStorageDao();

    public abstract UserDao getUserDao();

    public abstract VerificationTokenDao getVerificationTokenDao();

    public abstract SettingDao getSettingDao();

    public abstract BucketDao getBucketDao();

    public abstract ObjectMetadataDao getObjectMetadataDao();

    public abstract VersionedObjectDao getVersionedObjectDao();

    public abstract FileReferenceDao getFileReferenceDao();
}
