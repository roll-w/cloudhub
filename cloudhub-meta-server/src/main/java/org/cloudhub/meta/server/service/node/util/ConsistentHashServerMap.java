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

package org.cloudhub.meta.server.service.node.util;

import com.google.common.hash.Hashing;
import org.cloudhub.util.math.Maths;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A simple consistent hash mapping implementation.
 *
 * @author RollW
 */
public class ConsistentHashServerMap<S extends ConsistentHashServerMap.Server> {
    public static final int DISABLE_WEIGHT = -1;

    private static final int WEIGHT_VIRTUAL_NODES = 60;

    private final Map<String, S> serverPool = new HashMap<>();
    private final TreeMap<Long, String> hashServersPool = new TreeMap<>();
    private final List<ServerWeight<S>> serverWeights = new ArrayList<>();

    private final byte[] mLock = new byte[0];

    public void addServer(S server, int weight) {
        synchronized (mLock) {
            ServerWeight<S> serverWeight = new ServerWeight<>(server, weight);
            addServerToMap(serverWeight);
            serverWeights.add(serverWeight);
            remappingServers();
        }
    }

    public void setServerWeight(S server, int newWeight) {
        synchronized (mLock) {
            ServerWeight<S> serverWeight = new ServerWeight<>(server, newWeight);
            serverWeights.removeIf(sServerWeight ->
                    Objects.equals(sServerWeight.server.getId(), server.getId()));
            serverWeights.add(serverWeight);
            remappingServers();
        }
    }

    public void setServerWeights(List<ServerWeight<S>> serverWeights) {
        synchronized (mLock) {
            this.serverWeights.clear();
            this.serverWeights.addAll(serverWeights);

            serverWeights.forEach(this::addServerToMap);
            remappingServers();
        }
    }


    private void addServerToMap(ServerWeight<S> serverWeight) {
        if (serverWeight == null) {
            return;
        }
        serverPool.put(serverWeight.server.getId(), serverWeight.server);
    }

    private void remappingServers() {
        int totalWeight = 0;
        hashServersPool.clear();
        for (ServerWeight<S> server : serverWeights) {
            if (server.weight > DISABLE_WEIGHT) {
                totalWeight += server.weight;
            }
        }
        if (totalWeight == 0) {
            totalWeight = serverWeights.size();
        }
        for (ServerWeight<S> server : serverWeights) {
            int weight = 1;
            if (server.weight > DISABLE_WEIGHT) {
                weight = server.weight;
            }
            long nodes = mappingToVirtualNodes(weight, totalWeight, serverWeights.size());
            addNodesToHashServers(server.server, nodes);
        }
    }

    private void addNodesToHashServers(Server server, long nodes) {
        for (long i = 0; i < nodes; i++) {
            final String key = server.getId() + "-" + i;
            byte[] kBytes = Hashing.sha256().hashString(key, StandardCharsets.UTF_8).asBytes();
            for (int h = 0; h < 4; h++) {
                long k = ((long) (kBytes[3 + h * 4] & 0xFF) << 24)
                        | ((long) (kBytes[2 + h * 4] & 0xFF) << 16)
                        | ((long) (kBytes[1 + h * 4] & 0xFF) << 8)
                        | ((long) (kBytes[h * 4] & 0xFF << 1));
                hashServersPool.put(k, server.getId());
            }
        }
    }

    public void addServer(S server) {
        addServer(server, DISABLE_WEIGHT);
    }

    public void removeServer(S server) {
        if (server == null || server.getId() == null) {
            throw new NullPointerException();
        }
        synchronized (mLock) {
            serverWeights.removeIf(sServerWeight ->
                    Objects.equals(sServerWeight.server.getId(), server.getId()));
            remappingServers();
        }
    }

    public S allocateServer(String id) {
        // rehash key.
        long hash = hashKey(id);
        final int poolSize = hashServersPool.size();
        int tries = 0;
        while (tries++ < poolSize) {
            String serverId = hashServersPool.get(findNodeFor(hash));
            if (serverId != null) {
                return serverPool.get(serverId);
            }
            hash += Long.hashCode(hashKey(tries + id));
        }
        return null;
    }

    public Long findNodeFor(Long hash) {
        Long k = hashServersPool.ceilingKey(hash);
        // if none found, then it must be at the end.
        // returns the lowest server id in the tree
        if (k == null) {
            return hashServersPool.firstKey();
        }
        return k;
    }


    private long hashKey(String key) {
        byte[] hashBytes = Hashing.goodFastHash(64)
                .hashString(key, StandardCharsets.UTF_8)
                .asBytes();

        return takeHigh4Bytes(hashBytes);
    }

    private long takeHigh4Bytes(byte[] bytes) {
        return ((long) (bytes[3] & 0xFF) << 24)
                | ((long) (bytes[2] & 0xFF) << 16)
                | ((long) (bytes[1] & 0xFF) << 8)
                | ((long) bytes[0] & 0xFF << 1);
    }

    private int mappingToVirtualNodes(int weight, int totalWeight, int len) {
        return Maths.ceilDivide(WEIGHT_VIRTUAL_NODES * len * weight, totalWeight);
    }

    public interface Server {
        String getId();
    }

    public record ServerWeight<S extends Server>(
            S server,
            int weight) {
    }
}
