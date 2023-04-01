package org.huel.cloudhub.file.server.service.replica;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.huel.cloudhub.file.fs.block.BlockGroupsInfo;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerFinder;
import org.huel.cloudhub.file.fs.container.ContainerGroup;
import org.huel.cloudhub.file.rpc.replica.SerializedContainerCheckStatus;
import org.huel.cloudhub.file.rpc.synchro.DeleteContainerRequest;
import org.huel.cloudhub.file.rpc.synchro.DeleteContainerResponse;
import org.huel.cloudhub.file.rpc.synchro.SynchroRequest;
import org.huel.cloudhub.file.rpc.synchro.SynchroResponse;
import org.huel.cloudhub.file.rpc.synchro.SynchroServiceGrpc;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author RollW
 */
@Service
public class SynchroService extends SynchroServiceGrpc.SynchroServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(SynchroService.class);

    private final ContainerFinder containerFinder;
    private final CheckService checkService;
    private final ReplicaService replicaService;

    public SynchroService(ContainerFinder containerFinder,
                          CheckService checkService,
                          ReplicaService replicaService) {
        this.containerFinder = containerFinder;
        this.checkService = checkService;
        this.replicaService = replicaService;
    }

    @Override
    public void sendSynchro(SynchroRequest request, StreamObserver<SynchroResponse> responseObserver) {
        List<SerializedFileServer> servers = request.getServersList();
        String source = getSource(request);
        // pre-check all file exists
        for (String id : request.getFileIdsList()) {
            ContainerGroup containerGroup =
                    containerFinder.findContainerGroupByFile(id, source);
            if (containerGroup == null) {
                logger.error("File not found: {}", id);
                responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
                return;
            }
        }

        for (String fileId : request.getFileIdsList()) {
            ContainerGroup group = containerFinder.findContainerGroupByFile(fileId, source);
            servers.forEach(server ->
                    checkServerAndBuildReplica(group, fileId, server));
        }
        SynchroResponse synchroResponse = SynchroResponse.newBuilder()
                .build();
        responseObserver.onNext(synchroResponse);
        responseObserver.onCompleted();
    }

    private void checkServerAndBuildReplica(ContainerGroup group, String fileId,
                                            SerializedFileServer dest) {
        List<Container> containers = group.containersWithFile(fileId);
        List<Long> serials = containers
                .stream().map(Container::getSerial)
                .toList();

        List<SerializedContainerCheckStatus> statuses = checkService.sendContainerCheckRequest(
                group.getContainerId(),
                group.getSourceId(),
                serials,
                dest
        );

        Set<Long> needSync = new HashSet<>();
        List<Long> recvSerials = statuses.stream()
                .map(SerializedContainerCheckStatus::getSerial)
                .toList();
        for (SerializedContainerCheckStatus status : statuses) {
            long serial = status.getSerial();
            String recvCheck = status.getCheckValue();
            Container container = group.getContainer(serial);
            if (!Objects.equals(container.getIdentity().crc(), recvCheck)) {
                needSync.add(serial);
            }
        }
        serials.stream().filter(i -> !recvSerials.contains(i))
                .forEach(needSync::add);
        List<ReplicaSynchroPart> parts = needSync.stream()
                .map(group::getContainer)
                .filter(Objects::nonNull)
                .map(container -> new ReplicaSynchroPart(
                        container,
                        BlockGroupsInfo.build(0, container.getIdentity().blockLimit() - 1),
                        -1)
                )
                .toList();
        sendFullSyncRequest(parts, dest);
    }

    private void sendFullSyncRequest(List<ReplicaSynchroPart> parts,
                                     SerializedFileServer server) {
        replicaService.requestReplicasSynchro(parts, server);
    }

    private String getSource(SynchroRequest request) {
        if (request.hasSource()) {
            return request.getSource();
        }
        // if there's no source, then the current server must be the master.
        return ContainerFinder.LOCAL;
    }


    @Override
    public void deleteContainers(DeleteContainerRequest request,
                                 StreamObserver<DeleteContainerResponse> responseObserver) {
    }

}
