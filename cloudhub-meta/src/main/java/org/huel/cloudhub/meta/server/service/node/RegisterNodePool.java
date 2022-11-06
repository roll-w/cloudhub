package org.huel.cloudhub.meta.server.service.node;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class RegisterNodePool
        implements HeartbeatWatcherPool.ServerRemovable, NodeAllocator {
    private final Map<String, NodeServer> activeNodeServers =
            new ConcurrentHashMap<>();

    public RegisterNodePool() {
    }

    public void registerNodeServer(NodeServer nodeServer) {
        if (activeNodeServers.containsKey(nodeServer.id())) {
            return;
        }
        activeNodeServers.put(nodeServer.id(), nodeServer);
    }

    public List<NodeServer> getActiveNodes() {
        return List.copyOf(activeNodeServers.values());
    }

    public boolean isActive(String id) {
        return activeNodeServers.containsKey(id);
    }

    public boolean isActive(NodeServer nodeServer) {
        return activeNodeServers.containsKey(nodeServer.id());
    }

    public void removeNodeServer(NodeServer nodeServer) {
        removeNode(nodeServer.id());
    }

    public void removeNode(String id) {
        activeNodeServers.remove(id);
    }

    @Override
    public void removeActiveServer(NodeServer nodeServer) {
        removeNodeServer(nodeServer);
    }

    // TODO: allocate Node
    @Override
    public NodeServer allocateNode(String hash) {
        return null;
    }

    @Override
    public NodeServer allocateNode(long hash) {
        return null;
    }

    private long hashNode(NodeServer server) {
        return Hashing.sha256().hashString(server.id(), StandardCharsets.UTF_8)
                .padToLong();
    }
}
