package org.huel.cloudhub.server;

import oshi.hardware.NetworkIF;
import oshi.util.Util;

/**
 * @author RollW
 */
public class NetworkUsageInfo {
    private long recv;
    private long sent;
    private final long speed;

    public NetworkUsageInfo(long recv, long sent, long speed) {
        this.recv = recv;
        this.sent = sent;
        this.speed = speed;
    }

    public long getRecv() {
        return recv;
    }

    public long getSent() {
        return sent;
    }

    public long getSpeed() {
        return speed;
    }

    public NetworkUsageInfo reload(NetworkIF networkIF, long ms) {
        long lastRecv = networkIF.getBytesRecv();
        long lastSent = networkIF.getBytesSent();
        Util.sleep(ms);
        networkIF.updateAttributes();
        long nextRecv = networkIF.getBytesRecv();
        long nextSent = networkIF.getBytesSent();
        this.recv = ServerHostInfo.calcRate(lastRecv, nextRecv, ms);
        this.sent = ServerHostInfo.calcRate(lastSent, nextSent, ms);
        return this;
    }


    public static NetworkUsageInfo load(NetworkIF networkIF) {
        return new NetworkUsageInfo(
                0, 0,
                networkIF.getSpeed());
    }
}
