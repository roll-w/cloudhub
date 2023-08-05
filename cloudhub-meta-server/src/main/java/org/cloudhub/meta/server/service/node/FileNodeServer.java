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

import org.cloudhub.meta.server.service.node.util.ConsistentHashServerMap;
import org.cloudhub.file.rpc.heartbeat.Heartbeat;

import java.util.Objects;

/**
 * Represents a server node.
 *
 * @author RollW
 */
public final class FileNodeServer implements ConsistentHashServerMap.Server {
    private final String id;
    private final String host;
    private final int port;
    private final String address;

    public FileNodeServer(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.address = host + ":" + port;
    }

    public static FileNodeServer fromHeartbeat(Heartbeat heartbeat) {
        return new FileNodeServer(
                heartbeat.getId(),
                heartbeat.getHost(),
                heartbeat.getPort()
        );
    }

    @Override
    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FileNodeServer) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.host, that.host) &&
                this.port == that.port &&
                Objects.equals(this.address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port, address);
    }

    @Override
    public String toString() {
        return "NodeServer[" +
                "id=" + id + ", " +
                "host=" + host + ", " +
                "port=" + port + ", " +
                "address=" + address + ']';
    }

}
