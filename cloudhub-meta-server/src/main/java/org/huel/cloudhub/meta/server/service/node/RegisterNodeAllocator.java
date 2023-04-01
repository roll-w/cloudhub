package org.huel.cloudhub.meta.server.service.node;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.meta.server.service.node.util.ConsistentHashServerMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class RegisterNodeAllocator implements
        ServerEventRegistry.ServerEventCallback, NodeAllocator, NodeWeightUpdater {
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
        if (nodeServers.isEmpty()) {
            throw new NodeServerException("No file server connected.");
        }

        NodeServer nodeServer =  serverConsistentHashMap.allocateServer(fileId);
        if (nodeServer == null) {
            throw new NodeServerException("No file server available, " +
                    "probably all servers are down or no storage space available.");
        }

        return nodeServer;
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
        // refresh server's weight at a fixed rate
    }

    @Override
    public void addActiveServer(NodeServer nodeServer) {
        int weight = nodeWeightProvider.getWeightOf(nodeServer);
        serverConsistentHashMap.addServer(nodeServer, weight);
    }

    @Override
    public void onNewNodeWeight(String nodeId, int weight) {
        NodeServer nodeServer = nodeServers.get(nodeId);
        if (nodeServer == null) {
            return;
        }
        serverConsistentHashMap.setServerWeight(nodeServer, weight);
    }
}
