package org.huel.cloudhub.client.disk.database;

import org.huel.cloudhub.client.disk.database.dao.UserDao;
import org.huel.cloudhub.client.disk.domain.storage.DiskFileStorage;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;
import space.lingu.light.Database;
import space.lingu.light.LightDatabase;

/**
 * @author RollW
 */
@Database(name = "cloudhub_disk_database", version = 1, tables = {
        User.class,
        DiskFileStorage.class,
        UserFileStorage.class,
        UserDirectory.class,
        VersionedFileStorage.class
})
public abstract class DiskDatabase extends LightDatabase {
    public abstract UserDao getUserDao();
}
