package org.huel.cloudhub.meta.server.service.file;

import org.huel.cloudhub.meta.server.service.node.NodeServer;
import org.huel.cloudhub.meta.server.service.node.ServerEventRegistry;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class FileSynchroListener implements ServerEventRegistry.ServerEventCallback {
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
