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

package org.cloudhub.meta.server.data.entity;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;

/**
 * @author RollW
 */
@DataTable(tableName = "master_replica_location_table")
public class MasterReplicaLocation {
    @DataColumn(name = "container_id")
    @PrimaryKey
    private final String id;
    
    @DataColumn(name = "master_server_id")
    private final String master;
    
    @DataColumn(name = "replica_server_id")
    private String[] replicas;

    @DataColumn(name = "container_backup")
    @PrimaryKey
    private int backup = 0;

    public MasterReplicaLocation(String id, String master, String[] replicas) {
        this.id = id;
        this.master = master;
        this.replicas = replicas;
    }

    public MasterReplicaLocation(String id, String master, String[] replicas, int backup) {
        this.id = id;
        this.master = master;
        this.replicas = replicas;
        this.backup = backup;
    }

    public String getId() {
        return id;
    }

    public String getMaster() {
        return master;
    }

    public String[] getReplicas() {
        return replicas;
    }

    public void setReplicas(String[] replicas) {
        this.replicas = replicas;
    }

    public int getBackup() {
        return backup;
    }

    public void setBackup(int backup) {
        this.backup = backup;
    }

    private static final int ID_SUBNUM = 16;

    public static String toContainerId(String fileId) {
        if (fileId.length() <= ID_SUBNUM) {
            return fileId;
        }
        return fileId.substring(0, ID_SUBNUM);
    }
}
