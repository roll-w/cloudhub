package org.huel.cloudhub.meta.server.service.synchro;

import org.huel.cloudhub.file.rpc.synchro.SynchroRequest;
import org.huel.cloudhub.file.rpc.synchro.SynchroServiceGrpc;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeAllocator;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.meta.server.service.node.NodeServer;
import org.huel.cloudhub.meta.server.service.node.ServerChecker;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 同步服务
 *
 * @author RollW
 */
@Service
public class SynchroDispatchService {
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
    public void dispatchSynchro(SerializedFileServer fileServer, List<String> fileParts) {
        // need reduces the fileParts, or let the file-server to do it
        SynchroRequest request = SynchroRequest.newBuilder()
                .addAllFileIds(fileParts)
                .build();
        NodeServer server = nodeAllocator.findNodeServer(fileServer.getId());
        SynchroServiceGrpc.SynchroServiceStub synchroServiceStub =
                synchroServiceStubPool.getStub(server.id());
    }

}
