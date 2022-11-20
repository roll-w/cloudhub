package org.huel.cloudhub.meta.server.data.database.repository;

import org.huel.cloudhub.meta.server.data.database.MetaDatabase;
import org.huel.cloudhub.meta.server.data.database.dao.MasterReplicaLocationDao;
import org.huel.cloudhub.meta.server.data.entity.MasterReplicaLocation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class MasterReplicaLocationRepository {
    private final MasterReplicaLocationDao dao;

    public MasterReplicaLocationRepository(MetaDatabase metaDatabase) {
        dao = metaDatabase.getMasterReplicaLocationDao();
    }

    public void insertOrUpdate(MasterReplicaLocation masterReplicaLocation) {
        dao.insert(masterReplicaLocation);
    }

    public MasterReplicaLocation getByContainerId(String containerId) {
        return dao.getByContainerId(containerId);
    }

    @Async
    public void delete(String containerId) {
        dao.deleteById(containerId);
    }
}
