package org.huel.cloudhub.meta.server.service.synchro;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.rpc.synchro.SynchroRequest;
import org.huel.cloudhub.file.rpc.synchro.SynchroResponse;
import org.huel.cloudhub.file.rpc.synchro.SynchroServiceGrpc;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeAllocator;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.meta.server.service.node.NodeServer;
import org.huel.cloudhub.meta.server.service.node.NodeServerException;
import org.huel.cloudhub.meta.server.service.node.ServerChecker;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 同步服务
 *
 * @author RollW
 */
@Service
public class SynchroDispatchService {
    private static final Logger logger = LoggerFactory.getLogger(SynchroDispatchService.class);

    private final NodeChannelPool nodeChannelPool;
    private final GrpcServiceStubPool<SynchroServiceGrpc.SynchroServiceStub>
            synchroServiceStubPool;
    private final NodeAllocator nodeAllocator;
    private final ServerChecker serverChecker;

    public SynchroDispatchService(NodeChannelPool nodeChannelPool,
                                  HeartbeatService heartbeatService) {
        this.nodeChannelPool = nodeChannelPool;
        this.synchroServiceStubPool = new GrpcServiceStubPool<>();
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.serverChecker = heartbeatService.getServerChecker();
    }

    // tell the server, needs synchro which part of the data
    public void dispatchSynchro(NodeServer fileServer,
                                Collection<String> fileParts,
                                List<NodeServer> destServers) {
        List<SerializedFileServer> serializedServers =
                destServers.stream()
                        .map((server) ->
                                SerializedFileServer.newBuilder()
                                        .setId(server.getId())
                                        .setHost(server.host())
                                        .setPort(server.port())
                                        .build())
                        .toList();
        // need reduces the fileParts, or let the file-server to do it
        SynchroRequest request = SynchroRequest.newBuilder()
                .addAllFileIds(fileParts)
                .addAllServers(serializedServers)
                .build();
        if (!serverChecker.isActive(fileServer)) {
            logger.error("Synchro failed, server not active, serverId: {}",
                    fileServer.getId());
            throw new NodeServerException(
                    "Synchro failed, server not active, serverId: " +
                            fileServer.getId()
            );
        }

        SynchroServiceGrpc.SynchroServiceStub synchroServiceStub =
                getOrCreateStub(fileServer);
        synchroServiceStub.sendSynchro(request, new SynchroResponseObserver(fileServer));
    }

    private SynchroServiceGrpc.SynchroServiceStub getOrCreateStub(NodeServer server) {
        return synchroServiceStubPool.getStub(
                server.id(),
                () -> SynchroServiceGrpc.newStub(nodeChannelPool.getChannel(server))
        );
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class SynchroResponseObserver
            implements StreamObserver<SynchroResponse> {
        private final NodeServer server;

        public SynchroResponseObserver(NodeServer server) {
            this.server = server;
        }

        @Override
        public void onNext(SynchroResponse value) {
            // do nothing
        }

        @Override
        public void onError(Throwable t) {
            logger.error("Synchro failed, server: {}", server, t);
        }

        @Override
        public void onCompleted() {
            // do nothing
        }
    }

}
