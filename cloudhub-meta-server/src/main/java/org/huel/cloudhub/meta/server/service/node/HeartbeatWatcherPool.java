package org.huel.cloudhub.meta.server.service.node;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public final class HeartbeatWatcherPool implements ServerChecker, ServerEventRegistry {
    // lower latency, but more mem space occupied.
    private final Map<String, HeartbeatWatcher> heartbeatWatchers =
            new ConcurrentHashMap<>();
    private final Map<String, HeartbeatWatcher> activeHeartbeatWatchers =
            new ConcurrentHashMap<>();
    private final Map<String, HeartbeatWatcher> deadHeartbeatWatchers =
            new ConcurrentHashMap<>();
    private final Set<ServerEventCallback> serverEventCallbacks =
            new HashSet<>();

    private final int standardPeriod;
    private final int timeoutCycle;
    private final int timeoutTime;
    private final int frequency; // in ms.

    private final RCheckTimeoutRunnable checkTimeoutRunnable;

    public HeartbeatWatcherPool(int standardPeriod,
                                int timeoutCycle) {
        this.standardPeriod = standardPeriod;
        this.timeoutCycle = timeoutCycle;
        this.timeoutTime = standardPeriod * timeoutCycle;
        this.frequency = standardPeriod / 2;
        this.checkTimeoutRunnable = new RCheckTimeoutRunnable();
    }

    private void pushWatcher(HeartbeatWatcher heartbeatWatcher) {
        callRegisterServer(heartbeatWatcher.getNodeServer());
        heartbeatWatchers.put(heartbeatWatcher.getServerId(), heartbeatWatcher);
        setActiveWatcher(heartbeatWatcher);
        callAddActiveServer(heartbeatWatcher.getNodeServer());
    }

    public void pushNodeServerWatcher(NodeServer nodeServer) {

        pushWatcher(
                new HeartbeatWatcher(nodeServer, timeoutTime, System.currentTimeMillis())
        );
    }

    public void updateWatcher(Heartbeat heartbeat) {
        if (!heartbeatWatchers.containsKey(heartbeat.getId())) {
            return;
        }
        HeartbeatWatcher watcher = heartbeatWatchers.get(heartbeat.getId());
        watcher.updateHeartbeat(heartbeat);
        setActiveWatcher(watcher);
    }

    public HeartbeatWatcher getWatcher(String id) {
        return heartbeatWatchers.get(id);
    }

    public Collection<HeartbeatWatcher> activeWatchers() {
        return activeHeartbeatWatchers.values();
    }

    public Collection<HeartbeatWatcher> deadWatchers() {
        return deadHeartbeatWatchers.values();
    }

    @Override
    public Collection<NodeServer> getActiveServers() {
        return activeWatchers().stream()
                .map(HeartbeatWatcher::getNodeServer)
                .toList();
    }

    @Override
    public Collection<NodeServer> getDeadServers() {
        return deadWatchers().stream()
                .map(HeartbeatWatcher::getNodeServer)
                .toList();
    }

    @Override
    public int getActiveServerCount() {
        return activeHeartbeatWatchers.size();
    }

    @Override
    public boolean isActive(@Nullable NodeServer nodeServer) {
        if (nodeServer == null) {
            return false;
        }
        return isActive(nodeServer.id());
    }

    @Override
    public boolean isActive(@Nullable String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        long time = System.currentTimeMillis();
        HeartbeatWatcher watcher = getWatcher(id);
        if (watcher == null) {
            return false;
        }
        return !watcher.isTimeoutOrError(time);
    }

    public int getStandardPeriod() {
        return standardPeriod;
    }

    public int getTimeoutCycle() {
        return timeoutCycle;
    }

    public int getTimeoutTime() {
        return timeoutTime;
    }

    @Override
    public void registerCallback(ServerEventCallback serverEventCallback) {
        serverEventCallbacks.add(serverEventCallback);
    }


    private class RCheckTimeoutRunnable implements Runnable {
        @Override
        public void run() {
            long time = System.currentTimeMillis();
            heartbeatWatchers.values().stream().parallel().forEach(heartbeatWatcher -> {
                if (heartbeatWatcher.isTimeoutOrError(time)) {
                    setDeadWatcher(heartbeatWatcher);
                    callRemoveActiveServer(heartbeatWatcher.getNodeServer());
                }
            });
        }
    }

    private void setActiveWatcher(HeartbeatWatcher watcher) {
        if (watcher == null) {
            return;
        }
        if (activeHeartbeatWatchers.containsKey(watcher.getServerId())) {
            return;
        }
        deadHeartbeatWatchers.remove(watcher.getServerId());
        activeHeartbeatWatchers.put(watcher.getServerId(), watcher);
    }

    private void setDeadWatcher(HeartbeatWatcher watcher) {
        if (watcher == null) {
            return;
        }
        if (deadHeartbeatWatchers.containsKey(watcher.getServerId())) {
            return;
        }
        activeHeartbeatWatchers.remove(watcher.getServerId());
        deadHeartbeatWatchers.put(watcher.getServerId(), watcher);
    }

    public void start() {
        service.scheduleAtFixedRate(
                checkTimeoutRunnable,
                frequency / 2, frequency,
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        service.shutdown();
    }

    final ScheduledExecutorService service =
            Executors.newSingleThreadScheduledExecutor();

    private void callAddActiveServer(NodeServer nodeServer) {
        serverEventCallbacks.forEach(callback ->
                callback.addActiveServer(nodeServer));
    }

    private void callRemoveActiveServer(NodeServer nodeServer) {
        serverEventCallbacks.forEach(callback ->
                callback.removeActiveServer(nodeServer));
    }

    private void callRegisterServer(NodeServer nodeServer) {
        serverEventCallbacks.forEach(callback ->
                callback.registerServer(nodeServer));
    }
}
