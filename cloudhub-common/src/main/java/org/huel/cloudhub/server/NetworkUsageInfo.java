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

    public NetworkUsageInfo fork() {
        return new NetworkUsageInfo(recv, sent, speed);
    }

    protected NetworkUsageInfo reload(NetworkIF networkIF, long ms) {
        long prevRecv = networkIF.getBytesRecv();
        long prevSent = networkIF.getBytesSent();
        long prevTms = networkIF.getTimeStamp();
        Util.sleep(ms);
        networkIF.updateAttributes();
        long nextRecv = networkIF.getBytesRecv();
        long nextSent = networkIF.getBytesSent();
        long nextTms = networkIF.getTimeStamp();
        long diffTms = nextTms - prevTms;
        this.recv = ServerHostInfo.calcRate(prevRecv, nextRecv, diffTms);
        this.sent = ServerHostInfo.calcRate(prevSent, nextSent, diffTms);
        return this;
    }


    public static NetworkUsageInfo load(NetworkIF networkIF) {
        return new NetworkUsageInfo(
                0, 0,
                networkIF.getSpeed() / 8);
    }
}
