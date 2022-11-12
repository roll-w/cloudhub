package org.huel.cloudhub.meta.server.data.database;

import org.huel.cloudhub.meta.server.data.database.dao.FileStorageLocationDao;
import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import space.lingu.light.DataConverters;
import space.lingu.light.Database;
import space.lingu.light.LightDatabase;

/**
 * @author RollW
 */
@Database(name = "cloudhub_meta_database",
        version = 1,
        tables = {FileStorageLocation.class})
@DataConverters({MetaDataConverters.class})
public abstract class MetaDatabase extends LightDatabase {
    public abstract FileStorageLocationDao getFileObjectStorageLocationDao();
}
