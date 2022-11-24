package org.huel.cloudhub.file.server.service.server;

import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.rpc.container.ContainerStatusRequest;
import org.huel.cloudhub.file.rpc.container.ContainerStatusResponse;
import org.huel.cloudhub.file.rpc.container.ContainerStatusServiceGrpc;
import org.huel.cloudhub.file.rpc.container.SerializedContainerInfo;
import org.huel.cloudhub.file.server.service.container.ContainerService;
import org.huel.cloudhub.file.server.service.container.ReplicaContainerDelegate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RollW
 */
@Service
public class ContainerStatusService extends ContainerStatusServiceGrpc.ContainerStatusServiceImplBase {
    private final ContainerService containerService;
    private final ReplicaContainerDelegate replicaContainerDelegate;

    public ContainerStatusService(ContainerService containerService,
                                  ReplicaContainerDelegate replicaContainerDelegate) {
        this.containerService = containerService;
        this.replicaContainerDelegate = replicaContainerDelegate;
    }

    @Override
    public void getContainerInfo(ContainerStatusRequest request,
                                 StreamObserver<ContainerStatusResponse> responseObserver) {
        Collection<Container> locals = containerService.listContainers();
        Collection<Container> replicas = replicaContainerDelegate.listContainers();
        List<SerializedContainerInfo> serializes  = toSerializedStatus(locals);
        serializes.addAll(toSerializedStatus(replicas));
        responseObserver.onNext(ContainerStatusResponse.newBuilder()
                .addAllInfos(serializes)
                .build());
        responseObserver.onCompleted();
    }

    private List<SerializedContainerInfo> toSerializedStatus(Collection<Container> containers) {
        return containers.stream().map(container ->
                        SerializedContainerInfo.newBuilder()
                                .setContainerId(container.getIdentity().id())
                                .setSerial(container.getSerial())
                                .setLocator(container.getResourceLocator())
                                .setSource(container.getSource())
                                .setLimitBlocks(container.getIdentity().blockLimit())
                                .setLimitMbs((container.getIdentity().blockLimit() *
                                        container.getIdentity().blockSize()) / 1024)
                                .setUsedBlocks(container.getUsedBlocksCount())
                                .setBlockSize(container.getIdentity().blockSize())
                                .build())
                .collect(Collectors.toList());
    }
}
