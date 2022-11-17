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

    /**
     * If this piece of data exists, it means that both the master
     * and the replicas have been down, and the same file has been
     * uploaded to these file servers.
     * <p>
     * There is no need to carry the origin when making requests to
     * these file servers. When requesting to the replica,
     * it still carries the master id.
     */
    @DataColumn(name = "master_alias_server_id")
    private String[] aliasIds;

    public FileStorageLocation() {
    }

    public FileStorageLocation(String fileId, String masterServerId,
                               String[] replicaIds, String[] aliasIds) {
        this.fileId = fileId;
        this.masterServerId = masterServerId;
        this.replicaIds = replicaIds;
        this.aliasIds = aliasIds;
    }

    public String[] getAliasIds() {
        return aliasIds;
    }

    public void setAliasIds(String[] aliasIds) {
        this.aliasIds = aliasIds;
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

    public List<String> getServerList() {
        List<String> servers = new ArrayList<>();
        servers.add(masterServerId);
        if (replicaIds != null) {
            servers.addAll(Arrays.asList(replicaIds));
        }
        if (aliasIds != null) {
            servers.addAll(Arrays.asList(aliasIds));
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
        if (aliasIds != null) {
            for (String aliasId : aliasIds) {
                consumer.accept(aliasId, ServerType.ALIAS);
            }
        }
    }

    public enum ServerType {
        MASTER,
        REPLICA,
        ALIAS;
    }

    @Override
    public String toString() {
        return "FileStorageLocation{" +
                "fileId='" + fileId + '\'' +
                ", masterServerId='" + masterServerId + '\'' +
                ", replicaIds=" + Arrays.toString(replicaIds) +
                ", aliasIds=" + Arrays.toString(aliasIds) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileStorageLocation location = (FileStorageLocation) o;
        return Objects.equals(fileId, location.fileId) && Objects.equals(masterServerId, location.masterServerId) && Arrays.equals(replicaIds, location.replicaIds) && Arrays.equals(aliasIds, location.aliasIds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileId, masterServerId);
        result = 31 * result + Arrays.hashCode(replicaIds);
        result = 31 * result + Arrays.hashCode(aliasIds);
        return result;
    }
}
