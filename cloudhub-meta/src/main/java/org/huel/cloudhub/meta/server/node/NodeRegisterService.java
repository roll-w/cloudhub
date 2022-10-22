package org.huel.cloudhub.meta.server.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
@Service
public class NodeRegisterService implements HeartbeatWatcherPool.ServerRemovable {
    private final Map<String, NodeServer> activeNodeServerMap =
            new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(NodeRegisterService.class);

    public NodeRegisterService() {
    }

    public void registerNodeServer(NodeServer nodeServer) {
        if (activeNodeServerMap.containsKey(nodeServer.id())) {
            return;
        }
        activeNodeServerMap.put(nodeServer.id(), nodeServer);
    }

    public Collection<NodeServer> selectActiveNodes() {
        return activeNodeServerMap.values();
    }

    public boolean isActive(String id) {
        return activeNodeServerMap.containsKey(id);
    }

    public void removeNodeServer(NodeServer nodeServer) {
        removeNode(nodeServer.id());
    }

    public void removeNode(String id) {
        activeNodeServerMap.remove(id);
        logger.info("timeout server: id={}", id);
    }

    @Override
    public void removeActiveServer(NodeServer nodeServer) {
        removeNodeServer(nodeServer);
    }
}
