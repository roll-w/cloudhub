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

package org.cloudhub.server;

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
