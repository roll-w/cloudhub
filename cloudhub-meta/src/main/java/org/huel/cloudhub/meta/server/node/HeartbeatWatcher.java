package org.huel.cloudhub.meta.server.node;

import org.huel.cloudhub.server.rpc.proto.Heartbeat;

/**
 * @author RollW
 */
public class HeartbeatWatcher {
    private final NodeServer nodeServer;
    private final int timeoutTime;
    private volatile long lastHeartbeat;

    public HeartbeatWatcher(NodeServer nodeServer, int timeoutTime, long initialTime) {
        this.nodeServer = nodeServer;
        this.timeoutTime = timeoutTime;
        lastHeartbeat = initialTime;
    }

    public void updateHeartbeat(Heartbeat heartbeat) {
        lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isTimeout(long time) {
        return time - lastHeartbeat > timeoutTime;
    }

    public int getTimeoutTime() {
        return timeoutTime;
    }

    public String getServerId() {
        return nodeServer.getId();
    }

    public NodeServer getNodeServer() {
        return nodeServer;
    }
}
