package org.huel.cloudhub.meta.server.data.entity;

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
