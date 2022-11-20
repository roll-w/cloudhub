package org.huel.cloudhub.file.server.service.replica;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.fs.container.ContainerFinder;
import org.huel.cloudhub.file.rpc.replica.*;
import org.huel.cloudhub.file.server.service.ClientFileServerChannelPool;
import org.huel.cloudhub.rpc.GrpcServiceStubPool;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class CheckService {
    private final ClientFileServerChannelPool channelPool;
    private final GrpcServiceStubPool<CheckServiceGrpc.CheckServiceBlockingStub>
            checkServiceStubPool;

    public CheckService(ClientFileServerChannelPool channelPool) {
        this.channelPool = channelPool;
        this.checkServiceStubPool = new GrpcServiceStubPool<>();
    }

    public List<SerializedContainerStatus> sendContainerCheckRequest(String id, String source,
                                                                     List<Long> serials,
                                                                     SerializedFileServer dest) {
        CheckServiceGrpc.CheckServiceBlockingStub stub =
                requireCheckServiceStub(dest);
        CheckRequest.Builder requestBuilder = CheckRequest.newBuilder()
                .setContainerId(id)
                .addAllSerials(serials);
        if (!ContainerFinder.isLocal(source)) {
            requestBuilder.setSource(source);
        }
        CheckResponse response = stub.checkContainers(requestBuilder.build());
        return response.getStatusList();
    }

    private CheckServiceGrpc.CheckServiceBlockingStub requireCheckServiceStub(SerializedFileServer server) {
        ManagedChannel managedChannel = channelPool.getChannel(server);
        CheckServiceGrpc.CheckServiceBlockingStub stub =
                checkServiceStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        stub = CheckServiceGrpc.newBlockingStub(managedChannel);
        checkServiceStubPool.registerStub(server.getId(), stub);
        return stub;
    }
}
