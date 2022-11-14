package org.huel.cloudhub.meta.server.service.node;

import org.huel.cloudhub.meta.server.service.node.util.ConsistentHashServerMap;
import org.huel.cloudhub.rpc.heartbeat.Heartbeat;

/**
 * Represents a server node.
 *
 * @param id server id
 * @author RollW
 */
public record NodeServer(
        String id,
        String host,
        int port) implements ConsistentHashServerMap.Server {

    public String toAddress() {
        return host() + ":" + port();
    }

    public static NodeServer create(String id, String host, int port) {
        return new NodeServer(id, host, port);
    }

    public static NodeServer fromHeartbeat(Heartbeat heartbeat) {
        return new NodeServer(heartbeat.getId(), heartbeat.getHost(), heartbeat.getPort());
    }

    @Override
    public String getId() {
        return id();
    }
}
