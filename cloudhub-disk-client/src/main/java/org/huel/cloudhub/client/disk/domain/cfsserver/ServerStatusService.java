package org.huel.cloudhub.client.disk.domain.cfsserver;

import org.huel.cloudhub.client.CFSClient;
import org.huel.cloudhub.client.server.ConnectedServers;
import org.huel.cloudhub.server.NetworkUsageInfo;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.ServerStatusMonitor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
@Service
public class ServerStatusService {
    private final ServerStatusMonitor serverStatusMonitor;
    private final CFSClient cfsClient;
    private final AtomicLong runSecs = new AtomicLong(0);

    public ServerStatusService(CFSClient cfsClient) {
        this.cfsClient = cfsClient;
        serverStatusMonitor = new ServerStatusMonitor(".");
        serverStatusMonitor.setLimit(100);
        serverStatusMonitor.setRecordFrequency(1000);
        serverStatusMonitor.startMonitor();
    }

    public ServerHostInfo getCurrentInfo() {
        return serverStatusMonitor.getLatest();
    }

    public List<NetworkUsageInfo> getNetInfos() {
        return serverStatusMonitor.getRecent(50).stream()
                .map(ServerHostInfo::getNetworkUsageInfo)
                .toList();
    }

    @Scheduled(fixedRate = 10000)
    private void countUp() {
        runSecs.addAndGet(10);
    }

    public long getRunTimeLength() {
        return runSecs.get();
    }

    public ServerStatusSummary getSummary() {
        ConnectedServers connectedServers =
                cfsClient.getConnectedServers();
        return new ServerStatusSummary(
                runSecs.get(),
                connectedServers.activeServers().size(),
                connectedServers.deadServers().size()
        );
    }
}
