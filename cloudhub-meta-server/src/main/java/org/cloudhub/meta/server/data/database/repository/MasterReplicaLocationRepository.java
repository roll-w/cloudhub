/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.meta.server.data.database.repository;

import org.cloudhub.meta.server.data.entity.MasterReplicaLocation;
import org.cloudhub.meta.server.data.database.MetaDatabase;
import org.cloudhub.meta.server.data.database.dao.MasterReplicaLocationDao;
import org.cloudhub.meta.server.data.entity.MasterReplicaLocation;
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
