package org.huel.cloudhub.meta.server.service.node;

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
        return nodeServer.id();
    }

    public NodeServer getNodeServer() {
        return nodeServer;
    }

    /**
     */
    public HeartbeatWatcher fork() {
        return new HeartbeatWatcher(nodeServer, timeoutTime, lastHeartbeat);
    }

    @Override
    public String toString() {
        return "HeartbeatWatcher[" +
                "nodeServer=" + nodeServer +
                ", timeoutTime=" + timeoutTime +
                ", lastHeartbeat=" + lastHeartbeat +
                ']';
    }
}
