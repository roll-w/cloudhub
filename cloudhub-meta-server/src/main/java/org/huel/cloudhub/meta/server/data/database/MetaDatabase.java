package org.huel.cloudhub.meta.server.data.database;

import org.huel.cloudhub.meta.server.data.database.dao.FileStorageLocationDao;
import org.huel.cloudhub.meta.server.data.database.dao.MasterReplicaLocationDao;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.huel.cloudhub.meta.server.data.entity.MasterReplicaLocation;
import space.lingu.light.DataConverters;
import space.lingu.light.Database;
import space.lingu.light.LightDatabase;

/**
 * @author RollW
 */
@Database(name = "cloudhub_meta_database",
        tables = {FileStorageLocation.class, MasterReplicaLocation.class},
        version = 1)
@DataConverters({MetaDataConverters.class})
public abstract class MetaDatabase extends LightDatabase {
    public abstract FileStorageLocationDao getFileObjectStorageLocationDao();

    public abstract MasterReplicaLocationDao getMasterReplicaLocationDao();
}
