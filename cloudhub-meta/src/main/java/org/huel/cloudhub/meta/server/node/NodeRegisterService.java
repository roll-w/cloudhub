package org.huel.cloudhub.meta.server.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class NodeRegisterService implements HeartbeatWatcherPool.ServerRemovable {
    private final Map<String, NodeServer> activeNodeServerMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(NodeRegisterService.class);

    public NodeRegisterService() {
    }

    public void registerNodeServer(NodeServer nodeServer) {
        if (activeNodeServerMap.containsKey(nodeServer.getId())) {
            return;
        }
        activeNodeServerMap.put(nodeServer.getId(), nodeServer);
    }

    public Collection<NodeServer> selectActiveNodes() {
        return activeNodeServerMap.values();
    }

    public boolean isActive(String id) {
        return activeNodeServerMap.containsKey(id);
    }

    public void removeNodeServer(NodeServer nodeServer) {
        removeNode(nodeServer.getId());
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
