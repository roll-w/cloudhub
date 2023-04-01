package org.huel.cloudhub.meta.server.service.node;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.fs.status.StatusKeys;
import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatServiceGrpc;
import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.huel.cloudhub.server.rpc.status.SerializedServerStatusReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
@Service
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase
        implements NodeWeightProvider, ServerEventRegistry {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    private final HeartbeatServerProperties heartbeatServerProperties;
    private final HeartbeatWatcherPool heartbeatWatcherPool;
    private final RegisterNodeAllocator registerNodeAllocator;
    private final int timeoutTime;

    private final Map<String, Integer> weightMap = new HashMap<>();
    private final Map<String, AtomicInteger> counterMap = new HashMap<>();

    public HeartbeatService(HeartbeatServerProperties heartbeatServerProperties) {
        this.heartbeatServerProperties = heartbeatServerProperties;
        this.timeoutTime = heartbeatServerProperties.getTimeoutCycle() * heartbeatServerProperties.getStandardPeriod();
        this.registerNodeAllocator = new RegisterNodeAllocator(this);
        this.heartbeatWatcherPool = new HeartbeatWatcherPool(
                heartbeatServerProperties.getStandardPeriod(),
                heartbeatServerProperties.getTimeoutCycle());
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
            NodeServer nodeServer = NodeServer.fromHeartbeat(request);
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
        if (!containerReports.isEmpty()) {
            logger.warn("Node '{}' has damaged containers, containers: {}",
                    heartbeat.getId(),
                    containerReports
            );
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

    public Collection<NodeServer> activeServers() {
        return heartbeatWatcherPool.getActiveServers();
    }

    public NodeAllocator getNodeAllocator() {
        return registerNodeAllocator;
    }

    @Override
    public int getWeightOf(NodeServer nodeServer) {
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
}
