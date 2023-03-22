package org.huel.cloudhub.fs;

import org.huel.cloudhub.server.rpc.server.SerializedFileServer;

import java.util.List;

/**
 * @author RollW
 */
public class FileLocation {
    private final String fileId;
    private final String masterId;
    private final List<SerializedFileServer> servers;

    public FileLocation(String fileId,
                        String masterId,
                        List<SerializedFileServer> servers) {
        this.fileId = fileId;
        this.masterId = masterId;
        this.servers = servers;
    }

    public String getFileId() {
        return fileId;
    }

    public String getMasterId() {
        return masterId;
    }

    public List<SerializedFileServer> getServers() {
        return servers;
    }
}
