package org.huel.cloudhub.meta.server.service.file;

import org.huel.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.huel.cloudhub.meta.server.service.node.NodeServer;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public record RequestServer(NodeServer server,
                            FileStorageLocation.ServerType serverType) {

    public static List<SerializedFileServer> toSerialized(List<NodeServer> nodeServers) {
        List<SerializedFileServer> servers = new ArrayList<>();
        nodeServers.forEach(nodeServer -> servers.add(
                SerializedFileServer.newBuilder()
                        .setHost(nodeServer.host())
                        .setId(nodeServer.id())
                        .setPort(nodeServer.port())
                        .build()));
        return servers;
    }
}
