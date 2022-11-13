package org.huel.cloudhub.meta.server.service.node;

import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.server.rpc.status.SerializedServerStatusCode;

/**
 * @author RollW
 */
public class HeartbeatWatcher {
    private final NodeServer nodeServer;
    private final int timeoutTime;
    private volatile long lastHeartbeat;
    private volatile SerializedServerStatusCode statusCode;


    public HeartbeatWatcher(NodeServer nodeServer, int timeoutTime, long initialTime) {
        this.nodeServer = nodeServer;
        this.timeoutTime = timeoutTime;
        lastHeartbeat = initialTime;
    }

    public void updateHeartbeat(Heartbeat heartbeat) {
        statusCode = heartbeat.getStatusCode();
        lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isTimeout(long time) {
        return time - lastHeartbeat > (long) timeoutTime << 2;// 考虑到网络延迟
    }

    public boolean isTimeoutOrError(long time) {
        if (statusCode != SerializedServerStatusCode.HEALTHY) {
            return true;
        }
        return isTimeout(time);
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
