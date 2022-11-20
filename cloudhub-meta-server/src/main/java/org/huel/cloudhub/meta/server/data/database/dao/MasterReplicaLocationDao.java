package org.huel.cloudhub.meta.server.data.database.dao;

import org.huel.cloudhub.meta.server.data.entity.MasterReplicaLocation;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class MasterReplicaLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(MasterReplicaLocation... masterReplicaLocations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(MasterReplicaLocation masterReplicaLocations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<MasterReplicaLocation> masterReplicaLocations);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(MasterReplicaLocation... masterReplicaLocations);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<MasterReplicaLocation> masterReplicaLocations);

    @Delete
    public abstract void delete(MasterReplicaLocation... masterReplicaLocations);


    @Delete("DELETE FROM master_replica_location_table WHERE container_id = {containerId}")
    public abstract void deleteById(String containerId);

    @Query("SELECT * FROM master_replica_location_table WHERE container_id = {containerId}")
    public abstract MasterReplicaLocation getByContainerId(String containerId);
}
