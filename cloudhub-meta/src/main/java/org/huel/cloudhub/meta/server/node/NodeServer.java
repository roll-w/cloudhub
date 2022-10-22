package org.huel.cloudhub.meta.server.node;

import org.huel.cloudhub.server.rpc.proto.Heartbeat;

/**
 * @author RollW
 */
public class NodeServer {
    /**
     * UUID
     */
    private final String id;
    private final String host;
    private final int port;

    public NodeServer(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }


    public int getPort() {
        return port;
    }

    public String toAddress() {
        return getHost() + ":" + getPort();
    }

    public static NodeServer create(String id, String host, int port) {
        return new NodeServer(id, host, port);
    }

    public static NodeServer fromHeartbeat(Heartbeat heartbeat) {
        return new NodeServer(heartbeat.getId(), heartbeat.getHost(), heartbeat.getPort());
    }
}
