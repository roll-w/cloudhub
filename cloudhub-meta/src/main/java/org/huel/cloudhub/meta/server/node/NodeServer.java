package org.huel.cloudhub.meta.server.node;

import org.huel.cloudhub.server.rpc.proto.Heartbeat;

/**
 * Represents a server node.
 *
 * @param id server id
 * @author RollW
 */
public record NodeServer(String id, String host, int port) {

    public String toAddress() {
        return host() + ":" + port();
    }

    public static NodeServer create(String id, String host, int port) {
        return new NodeServer(id, host, port);
    }

    public static NodeServer fromHeartbeat(Heartbeat heartbeat) {
        return new NodeServer(heartbeat.getId(), heartbeat.getHost(), heartbeat.getPort());
    }
}
