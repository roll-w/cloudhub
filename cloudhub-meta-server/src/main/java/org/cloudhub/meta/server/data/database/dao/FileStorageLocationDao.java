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

package org.cloudhub.meta.server.data.database.dao;

import org.cloudhub.meta.server.data.entity.FileStorageLocation;
import space.lingu.light.*;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public abstract class FileStorageLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FileStorageLocation... fileStorageLocations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FileStorageLocation fileStorageLocation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<FileStorageLocation> fileStorageLocations);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(FileStorageLocation... fileStorageLocations);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<FileStorageLocation> fileStorageLocations);

    @Delete
    public abstract void delete(FileStorageLocation... fileStorageLocations);


    @Delete("DELETE FROM file_storage_location_table WHERE file_id = {fileId}")
    public abstract void deleteById(String fileId);

    @Delete("DELETE FROM file_storage_location_table WHERE file_id = {fileId} AND file_backup = {backup}")
    public abstract void deleteById(String fileId, int backup);

    @Query("SELECT * FROM file_storage_location_table WHERE file_id = {fileId} ORDER BY file_backup DESC LIMIT 1")
    public abstract FileStorageLocation getByFileId(String fileId);

    @Query("SELECT * FROM file_storage_location_table WHERE file_id = {fileId} ORDER BY file_backup DESC")
    public abstract List<FileStorageLocation> getLocationsByFileIdDesc(String fileId);

    @Query("SELECT * FROM file_storage_location_table WHERE file_id = {fileId} ORDER BY file_backup DESC")
    public abstract List<FileStorageLocation> getLocationsByFileId(String fileId);

    @Query("SELECT * FROM file_storage_location_table WHERE master_server_id = {serverId} OR replica_server_id LIKE '%'|| {serverId} || '%' ")
    public abstract List<FileStorageLocation> getLocationsByServerId(String serverId);
}
