package org.huel.cloudhub.meta.server.node;

import org.huel.cloudhub.server.rpc.proto.Heartbeat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public final class HeartbeatWatcherPool {
    private final Map<String, HeartbeatWatcher> activeHeartbeatWatchers =
            new ConcurrentHashMap<>();

    private final int standardPeriod;
    private final int timeoutCycle;
    private final int timeoutTime;
    private final ServerRemovable serverRemovable;
    private final int frequency; // in ms.

    private final RCheckTimeoutRunnable checkTimeoutRunnable;

    public HeartbeatWatcherPool(int standardPeriod,
                                int timeoutCycle,
                                ServerRemovable removable) {
        this.standardPeriod = standardPeriod;
        this.timeoutCycle = timeoutCycle;
        this.serverRemovable = removable;
        this.timeoutTime = standardPeriod * timeoutCycle;
        frequency = standardPeriod / 2;
        this.checkTimeoutRunnable = new RCheckTimeoutRunnable();
    }

    public void pushWatcher(HeartbeatWatcher heartbeatWatcher) {
        activeHeartbeatWatchers.put(heartbeatWatcher.getServerId(), heartbeatWatcher);
    }

    public void pushNodeServerWatcher(NodeServer nodeServer) {
        pushWatcher(
                new HeartbeatWatcher(nodeServer, timeoutTime, System.currentTimeMillis())
        );
    }

    public void updateWatcher(Heartbeat heartbeat) {
        if (!activeHeartbeatWatchers.containsKey(heartbeat.getId())) {
            return;
        }
        activeHeartbeatWatchers.get(heartbeat.getId())
                .updateHeartbeat(heartbeat);
    }

    public HeartbeatWatcher getWatcher(String id) {
        return activeHeartbeatWatchers.get(id);
    }

    private class RCheckTimeoutRunnable implements Runnable {
        @Override
        public void run() {
            long time = System.currentTimeMillis();
            activeHeartbeatWatchers.values().stream().parallel().forEach(heartbeatWatcher -> {
                if (heartbeatWatcher.isTimeout(time)) {
                    serverRemovable.removeActiveServer(heartbeatWatcher.getNodeServer());
                    activeHeartbeatWatchers.remove(heartbeatWatcher.getServerId());
                }
            });
        }
    }

    public void start() {
        service.scheduleAtFixedRate(
                checkTimeoutRunnable,
                10, frequency,
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        service.shutdown();
    }

    final ScheduledExecutorService service =
            Executors.newSingleThreadScheduledExecutor();

    public interface ServerRemovable {
        void removeActiveServer(NodeServer nodeServer);
    }
}
