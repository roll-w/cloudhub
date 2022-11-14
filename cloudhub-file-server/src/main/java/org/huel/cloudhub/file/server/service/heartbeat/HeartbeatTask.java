package org.huel.cloudhub.file.server.service.heartbeat;

import org.huel.cloudhub.rpc.heartbeat.HeartbeatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Component
public class HeartbeatTask {
    private final ServerHeartbeatService service;
    private final HeartbeatHostProperties properties;

    private final Logger logger = LoggerFactory.getLogger(HeartbeatTask.class);

    public HeartbeatTask(ServerHeartbeatService service,
                         HeartbeatHostProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    public void startSendHeartbeat() {
        HeartbeatResponse response = service.sendHeartbeat();
        int period;
        if (!response.hasPeriod()) {
            period = properties.getHeartbeatPeriod();
        } else {
            period = response.getPeriod();
        }
        logger.info("First receive heartbeat response, set period to {}.", period);
        if (period <= 0) {
            throw new IllegalStateException("Not has a legal period.");
        }

        scheduledExecutorService.scheduleAtFixedRate(
                service::sendHeartbeat,
                0,
                period,
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }

    final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();
}
