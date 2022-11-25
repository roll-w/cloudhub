package org.huel.cloudhub.meta.server.service.synchro;

import org.huel.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeAllocator;
import org.huel.cloudhub.meta.server.service.node.NodeServer;
import org.huel.cloudhub.meta.server.service.node.ServerChecker;
import org.huel.cloudhub.meta.server.service.node.ServerEventRegistry;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class FileSynchroListener implements ServerEventRegistry.ServerEventCallback {
    private final FileStorageLocationRepository fileRepository;
    private final NodeAllocator nodeAllocator;
    private final ServerChecker serverChecker;

    public FileSynchroListener(FileStorageLocationRepository fileRepository,
                               HeartbeatService heartbeatService) {
        this.fileRepository = fileRepository;
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.serverChecker = heartbeatService.getServerChecker();
    }

    @Override
    public void registerServer(NodeServer server) {
    }

    @Override
    public void removeActiveServer(NodeServer nodeServer) {
    }

    @Override
    public void addActiveServer(NodeServer nodeServer) {
        // check the
    }
}
