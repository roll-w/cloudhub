package org.huel.cloudhub.client.disk.domain.cfsserver;

import org.huel.cloudhub.client.CFSClient;
import org.huel.cloudhub.client.CFSStatus;
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
        ConnectedServers connectedServers;
        ServerHostInfo serverHostInfo =
                serverStatusMonitor.getLatest();
        try {
            connectedServers = cfsClient.getConnectedServers();
        } catch (Exception e) {
            return getSummaryOf(
                    runSecs.get(),
                    CFSStatus.UNAVAILABLE,
                    0,
                    0,
                    serverHostInfo
            );
        }
        return getSummaryOf(
                runSecs.get(),
                CFSStatus.SUCCESS,
                connectedServers.activeServers().size(),
                connectedServers.deadServers().size(),
                serverHostInfo
        );
    }

    private static ServerStatusSummary getSummaryOf(long runtime,
                                                    CFSStatus cfsStatus,
                                                    int activeFileServers,
                                                    int deadFileServers,
                                                    ServerHostInfo serverHostInfo) {
        long diskUsed = serverHostInfo.getDiskUsageInfo().getTotal() -
                serverHostInfo.getDiskUsageInfo().getFree();
        return new ServerStatusSummary(
                runtime,
                cfsStatus,
                activeFileServers,
                deadFileServers,
                new UsageInfo(
                        serverHostInfo.getDiskUsageInfo().getTotal(),
                        diskUsed
                ),
                new UsageInfo(
                        serverHostInfo.getMemoryUsageInfo().getTotal(),
                        serverHostInfo.getMemoryUsageInfo().getUsed()
                )
        );
    }
}
