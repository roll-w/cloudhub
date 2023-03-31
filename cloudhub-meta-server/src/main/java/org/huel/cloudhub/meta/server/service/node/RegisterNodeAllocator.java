package org.huel.cloudhub.meta.server.service.node;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.meta.server.service.node.util.ConsistentHashServerMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class RegisterNodeAllocator implements
        ServerEventRegistry.ServerEventCallback, NodeAllocator {
    private final NodeWeightProvider nodeWeightProvider;
    private final Map<String, NodeServer> nodeServers =
            new ConcurrentHashMap<>();
    private final ConsistentHashServerMap<NodeServer> serverConsistentHashMap =
            new ConsistentHashServerMap<>();

    public RegisterNodeAllocator(NodeWeightProvider nodeWeightProvider) {
        this.nodeWeightProvider = nodeWeightProvider;
    }

    @Override
    public void registerNodeServer(NodeServer nodeServer) {
        if (nodeServers.containsKey(nodeServer.id())) {
            return;
        }
        nodeServers.put(nodeServer.id(), nodeServer);
    }

    @Override
    public NodeServer allocateNode(String fileId) {
        return serverConsistentHashMap.allocateServer(fileId);
    }

    @Override
    @Nullable
    public NodeServer findNodeServer(String serverId) {
        return nodeServers.get(serverId);
    }

    @Override
    public void registerServer(NodeServer server) {
        registerNodeServer(server);
    }

    @Override
    public void removeActiveServer(NodeServer nodeServer) {
        serverConsistentHashMap.removeServer(nodeServer);
    }

    @Override
    public void addActiveServer(NodeServer nodeServer) {
        int weight = nodeWeightProvider.getWeightOf(nodeServer);
        serverConsistentHashMap.addServer(nodeServer, weight);
    }
}
