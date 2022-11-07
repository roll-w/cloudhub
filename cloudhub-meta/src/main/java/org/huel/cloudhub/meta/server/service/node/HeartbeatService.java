package org.huel.cloudhub.meta.server.service.node;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class HeartbeatService extends HeartbeatServiceGrpc.HeartbeatServiceImplBase implements NodeWeightProvider {
    private final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    private final HeartbeatServerProperties heartbeatServerProperties;
    private final HeartbeatWatcherPool heartbeatWatcherPool;
    private final RegisterNodeAllocator registerNodeAllocator;
    private final int timeoutTime;

    public HeartbeatService(HeartbeatServerProperties heartbeatServerProperties) {
        this.heartbeatServerProperties = heartbeatServerProperties;
        this.timeoutTime = heartbeatServerProperties.getTimeoutCycle() * heartbeatServerProperties.getStandardPeriod();
        this.registerNodeAllocator = new RegisterNodeAllocator(this);
        this.heartbeatWatcherPool = new HeartbeatWatcherPool(
                heartbeatServerProperties.getStandardPeriod(),
                heartbeatServerProperties.getTimeoutCycle(),
                registerNodeAllocator);
        heartbeatWatcherPool.start();
        // or HeartbeatConfiguration?
    }

    @Override
    public void receiveHeartbeat(Heartbeat request, StreamObserver<HeartbeatResponse> responseObserver) {
//        logger.info("receive heartbeat, address: {}:{}, id: {}",
//                request.getHost(), request.getPort(), request.getId());
        if (!heartbeatWatcherPool.isActive(request.getId())) {
            responseObserver.onNext(
                    HeartbeatResponse.newBuilder()
                            .setErrorCode("00000")
                            .setMessage("first time registration.")
                            .setPeriod(heartbeatServerProperties.getStandardPeriod())
                            .build()
            );
            NodeServer nodeServer = NodeServer.fromHeartbeat(request);
            heartbeatWatcherPool.pushNodeServerWatcher(nodeServer);

            responseObserver.onCompleted();
            return;
        }
        heartbeatWatcherPool.updateWatcher(request);
        responseObserver.onNext(
                HeartbeatResponse.newBuilder()
                        .setErrorCode("00000")
                        .setMessage("update success.")
                        .build()
        );
        responseObserver.onCompleted();
    }

    public List<HeartbeatWatcher> activeHeartbeatWatchers() {
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
        // TODO:
        return 1;
    }
}
