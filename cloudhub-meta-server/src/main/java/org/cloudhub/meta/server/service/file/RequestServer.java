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

package org.cloudhub.meta.server.service.file;

import org.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.cloudhub.meta.server.service.node.FileNodeServer;
import org.cloudhub.server.rpc.server.SerializedFileServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author RollW
 */
public record RequestServer(FileNodeServer server,
                            FileStorageLocation.ServerType serverType) {

    public static List<SerializedFileServer> toSerialized(Collection<FileNodeServer> nodeServers) {
        List<SerializedFileServer> servers = new ArrayList<>();
        nodeServers.forEach(nodeServer ->
                servers.add(toSerialized(nodeServer)));
        return servers;
    }

    public static SerializedFileServer toSerialized(FileNodeServer nodeServer) {
        return SerializedFileServer.newBuilder()
                .setHost(nodeServer.getHost())
                .setId(nodeServer.getId())
                .setPort(nodeServer.getPort())
                .build();
    }
}
