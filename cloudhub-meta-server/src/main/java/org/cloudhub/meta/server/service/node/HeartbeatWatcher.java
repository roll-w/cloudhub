/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.meta.server.service.node;

import org.cloudhub.file.rpc.heartbeat.Heartbeat;
import org.cloudhub.file.rpc.status.SerializedServerStatusCode;

/**
 * @author RollW
 */
public class HeartbeatWatcher {
    private final FileNodeServer nodeServer;
    private final int timeoutTime;
    private volatile long lastHeartbeat;
    private volatile SerializedServerStatusCode statusCode;
    private static final int TIMEOUT_COMPENSATION = 1000;

    public HeartbeatWatcher(FileNodeServer nodeServer, int timeoutTime, long initialTime) {
        this.nodeServer = nodeServer;
        this.timeoutTime = timeoutTime;
        lastHeartbeat = initialTime;
    }

    public void updateHeartbeat(Heartbeat heartbeat) {
        statusCode = heartbeat.getStatusCode();
        lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isTimeout(long time) {
        return time - lastHeartbeat > (long) timeoutTime + TIMEOUT_COMPENSATION;// 考虑到网络延迟
    }

    public boolean isTimeoutOrError(long time) {
        if (statusCode == SerializedServerStatusCode.DOWN) {
            return true;
        }
        return isTimeout(time);
    }

    public int getTimeoutTime() {
        return timeoutTime;
    }

    public String getServerId() {
        return nodeServer.getId();
    }

    public FileNodeServer getNodeServer() {
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
