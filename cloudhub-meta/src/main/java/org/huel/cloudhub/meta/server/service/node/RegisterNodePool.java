package org.huel.cloudhub.meta.server.service.node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class RegisterNodePool implements HeartbeatWatcherPool.ServerRemovable {
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
}
