package org.huel.cloudhub.meta.server.data.entity;

import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author RollW
 */
@DataTable(tableName = "file_storage_location_table")
public class FileStorageLocation {
    @DataColumn(name = "file_id")
    @PrimaryKey
    private String fileId;

    @DataColumn(name = "master_server_id")
    private String masterServerId;

    @DataColumn(name = "replica_server_id")
    private String[] replicaIds;

    @DataColumn(name = "file_backup")
    @PrimaryKey
    private int backup = 0;

    public FileStorageLocation() {
    }

    public FileStorageLocation(String fileId, String masterServerId,
                               String[] replicaIds) {
        this.fileId = fileId;
        this.masterServerId = masterServerId;
        this.replicaIds = replicaIds;
    }

    public FileStorageLocation(String fileId, String masterServerId,
                               String[] replicaIds, int backup) {
        this.fileId = fileId;
        this.masterServerId = masterServerId;
        this.replicaIds = replicaIds;
        this.backup = backup;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getMasterServerId() {
        return masterServerId;
    }

    public void setMasterServerId(String masterServerId) {
        this.masterServerId = masterServerId;
    }

    public String[] getReplicaIds() {
        return replicaIds;
    }

    public void setReplicaIds(String[] replicaIds) {
        this.replicaIds = replicaIds;
    }

    public int getBackup() {
        return backup;
    }

    public void setBackup(int backup) {
        this.backup = backup;
    }

    public List<String> getServerList() {
        List<String> servers = new ArrayList<>();
        servers.add(masterServerId);
        if (replicaIds != null) {
            servers.addAll(Arrays.asList(replicaIds));
        }
        return servers;
    }

    public void getServersWithType(BiConsumer<String, ServerType> consumer) {
        consumer.accept(masterServerId, ServerType.MASTER);
        if (replicaIds != null) {
            for (String replicaId : replicaIds) {
                consumer.accept(replicaId, ServerType.REPLICA);
            }
        }
    }

    public enum ServerType {
        MASTER,
        REPLICA,
    }

    @Override
    public String toString() {
        return "FileStorageLocation{" +
                "fileId='" + fileId + '\'' +
                ", masterServerId='" + masterServerId + '\'' +
                ", replicaIds=" + Arrays.toString(replicaIds) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileStorageLocation location = (FileStorageLocation) o;
        return Objects.equals(fileId, location.fileId) && Objects.equals(masterServerId, location.masterServerId) && Arrays.equals(replicaIds, location.replicaIds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileId, masterServerId);
        result = 31 * result + Arrays.hashCode(replicaIds);
        return result;
    }
}
