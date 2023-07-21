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

import org.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.cloudhub.meta.server.data.database.MetaDatabase;
import org.cloudhub.meta.server.data.database.dao.FileStorageLocationDao;
import org.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class FileStorageLocationRepository {
    private final FileStorageLocationDao dao;

    public FileStorageLocationRepository(MetaDatabase metaDatabase) {
        dao = metaDatabase.getFileObjectStorageLocationDao();
    }

    public void insertOrUpdate(FileStorageLocation storageLocation) {
        dao.insert(storageLocation);
    }

    public FileStorageLocation getByFileId(String fileId) {
        return dao.getByFileId(fileId);
    }

    @Async
    public void delete(String fileId) {
        dao.deleteById(fileId);
    }

    @Async
    public void delete(FileStorageLocation... location) {
        dao.delete(location);
    }

    public List<FileStorageLocation> getLocationsByFileIdDesc(String fileId) {
        return dao.getLocationsByFileIdDesc(fileId);
    }

    public List<FileStorageLocation> getLocationsByFileId(String fileId) {
        return dao.getLocationsByFileId(fileId);
    }

    public List<FileStorageLocation> getLocationsByServerId(String serverId) {
        return dao.getLocationsByServerId(serverId);
    }
}
