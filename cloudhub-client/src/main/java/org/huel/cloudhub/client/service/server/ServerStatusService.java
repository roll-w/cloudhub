package org.huel.cloudhub.client.service.server;

import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.ServerStatusMonitor;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class ServerStatusService {
    private final ServerStatusMonitor serverStatusMonitor;

    public ServerStatusService() {
        serverStatusMonitor = new ServerStatusMonitor(".");
        serverStatusMonitor.setLimit(500);
        serverStatusMonitor.setRecordFrequency(500);
        serverStatusMonitor.startMonitor();
    }

    public ServerHostInfo getCurrentInfo() {
        return serverStatusMonitor.getLatest();
    }
}
