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

import io.grpc.stub.StreamObserver;
import org.cloudhub.fs.status.StatusKeys;
import org.cloudhub.file.rpc.heartbeat.Heartbeat;
import org.cloudhub.file.rpc.heartbeat.HeartbeatResponse;
import org.cloudhub.file.rpc.heartbeat.HeartbeatServiceGrpc;
import org.cloudhub.file.rpc.status.SerializedDamagedContainerReport;
import org.cloudhub.file.rpc.status.SerializedServerStatusReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
@Service
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase
        implements NodeWeightProvider, ServerEventRegistry, ContainerStatusProvider,
        ServerEventRegistry.ServerEventCallback {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    private final HeartbeatServerProperties heartbeatServerProperties;
    private final HeartbeatWatcherPool heartbeatWatcherPool;
    private final RegisterNodeAllocator registerNodeAllocator;
    private final int timeoutTime;

    private final Map<String, Integer> weightMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    private final Map<String, List<SerializedDamagedContainerReport>> nodeDamagedContainerReports
            = new ConcurrentHashMap<>();

    public HeartbeatService(HeartbeatServerProperties heartbeatServerProperties) {
        this.heartbeatServerProperties = heartbeatServerProperties;
        this.timeoutTime = heartbeatServerProperties.getTimeoutCycle() * heartbeatServerProperties.getStandardPeriod();
        this.registerNodeAllocator = new RegisterNodeAllocator(this);
        this.heartbeatWatcherPool = new HeartbeatWatcherPool(
                heartbeatServerProperties.getStandardPeriod(),
                heartbeatServerProperties.getTimeoutCycle());
        heartbeatWatcherPool.registerCallback(this);
        heartbeatWatcherPool.registerCallback(registerNodeAllocator);
        heartbeatWatcherPool.start();
    }

    @Override
    public void receiveHeartbeat(Heartbeat request, StreamObserver<HeartbeatResponse> responseObserver) {
        AtomicInteger counter = counterMap.computeIfAbsent(
                request.getId(),
                k -> new AtomicInteger(0)
        );

        if (!heartbeatWatcherPool.isActive(request.getId())) {
            counter.set(0);
            responseObserver.onNext(
                    HeartbeatResponse.newBuilder()
                            .setPeriod(heartbeatServerProperties.getStandardPeriod())
                            .build()
            );
            FileNodeServer nodeServer = FileNodeServer.fromHeartbeat(request);
            heartbeatWatcherPool.pushNodeServerWatcher(nodeServer);

            responseObserver.onCompleted();
            return;
        }
        int time = counter.incrementAndGet();
        boolean isStatusTime = isStatusTime(time);
        if (isStatusTime) {
            counter.set(0);
        }
        checkAndUpdateWeight(request);
        heartbeatWatcherPool.updateWatcher(request);
        responseObserver.onNext(
                HeartbeatResponse.newBuilder()
                        .setContainsStatNext(isStatusTime)
                        .build()
        );
        responseObserver.onCompleted();
    }

    private boolean isStatusTime(int times) {
        return times % 20 == 0;
    }

    private void checkAndUpdateWeight(Heartbeat heartbeat) {
        if (!heartbeat.hasStatus()) {
            return;
        }
        SerializedServerStatusReport statusReport = heartbeat.getStatus();

        List<SerializedDamagedContainerReport> containerReports =
                statusReport.getReportList();

        // should not be any other thread to access this map.
        nodeDamagedContainerReports.put(heartbeat.getId(), containerReports);

        if (!containerReports.isEmpty()) {
            // logger.warn("Node '{}' has damaged containers, containers: {}",
            //         heartbeat.getId(),
            //         containerReports
            // );
        }

        Map<String, String> status = statusReport.getStatusMap();

        long remainSpace = Long.parseLong(status.get(StatusKeys.STORAGE_REMAINING));
        int weight = scaleDiskSize(remainSpace);

        Integer lastWeight = weightMap.get(heartbeat.getId());
        weightMap.put(heartbeat.getId(), weight);
        if (lastWeight != null && lastWeight != weight) {
            logger.info("Node '{}' disk size changed, last weight: {}, current weight: {}",
                    heartbeat.getId(),
                    lastWeight,
                    weight
            );
            registerNodeAllocator.onNewNodeWeight(heartbeat.getId(), weight);
        }
    }

    public Collection<HeartbeatWatcher> activeHeartbeatWatchers() {
        return heartbeatWatcherPool.activeWatchers();
    }

    public Collection<FileNodeServer> activeServers() {
        return heartbeatWatcherPool.getActiveServers();
    }

    public NodeAllocator getNodeAllocator() {
        return registerNodeAllocator;
    }

    @Override
    public int getWeightOf(FileNodeServer nodeServer) {
        Integer weight = weightMap.get(nodeServer.getId());
        if (weight == null) {
            return 1;
        }
        return weight;
    }

    // TODO: set by file-server configuration

    // at least remains 64MB
    private static final long MINIMUM_DISK_SIZE = 64;

    private int scaleDiskSize(long freeSpaceInBytes) {
        long spaceInMb = freeSpaceInBytes / 1024 / 1024;
        if (spaceInMb <= MINIMUM_DISK_SIZE) {
            return 0;
        }
        long scale = spaceInMb / MINIMUM_DISK_SIZE;

        return (int) scale;
    }

    public ServerChecker getServerChecker() {
        return heartbeatWatcherPool;
    }

    @Override
    public void registerCallback(ServerEventCallback serverEventCallback) {
        heartbeatWatcherPool.registerCallback(serverEventCallback);
    }

    @Override
    public List<SerializedDamagedContainerReport> getDamagedContainerReports(
            FileNodeServer nodeServer) {
        return nodeDamagedContainerReports.get(nodeServer.getId());
    }

    @Override
    public List<ServerContainerStatus> getDamagedContainerReports() {
        return nodeDamagedContainerReports
                .entrySet()
                .stream()
                .map(entry ->
                        new ServerContainerStatus(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public void registerServer(FileNodeServer server) {
    }

    @Override
    public void removeActiveServer(FileNodeServer nodeServer) {
        nodeDamagedContainerReports.remove(nodeServer.getId());
        weightMap.put(nodeServer.getId(), 0);
    }

    @Override
    public void addActiveServer(FileNodeServer nodeServer) {
    }
}
