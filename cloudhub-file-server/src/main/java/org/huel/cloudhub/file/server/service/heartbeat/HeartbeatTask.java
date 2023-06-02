package org.huel.cloudhub.file.server.service.heartbeat;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.huel.cloudhub.file.server.service.ContainerDiagnosable;
import org.huel.cloudhub.file.server.service.Monitorable;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
@Service
public class HeartbeatTask {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatTask.class);

    private final HeartbeatSender service;
    private final HeartbeatHostProperties properties;

    private final AtomicBoolean start = new AtomicBoolean(false);

    private final AtomicInteger nowPeriod = new AtomicInteger();
    private final AtomicInteger errorCount = new AtomicInteger();

    public HeartbeatTask(ManagedChannel managedChannel,
                         ContainerDiagnosable containerDiagnosable,
                         Monitorable monitorable,
                         SourceServerGetter sourceServerGetter,
                         HeartbeatHostProperties properties) {
        this.service = new HeartbeatSender(
                managedChannel,
                containerDiagnosable.getDiagnosable(),
                sourceServerGetter,
                monitorable
        );
        this.properties = properties;
    }

    public void startSendHeartbeat() {
        int period = sendAndCheckHeartbeat();
        start.set(true);
        scheduledExecutorService.schedule(
                new HeartbeatTaskRunnable(),
                period,
                TimeUnit.MILLISECONDS
        );
    }

    private class HeartbeatTaskRunnable implements Runnable {
        @Override
        public void run() {
            int period = sendAndCheckHeartbeat();
            if (!start.get()) {
                return;
            }
            scheduledExecutorService.schedule(
                    this, period, TimeUnit.MILLISECONDS
            );
        }
    }

    private int sendAndCheckHeartbeat() {
        HeartbeatResponse response;
        try {
            response = service.sendHeartbeat();
        } catch (Exception e) {
            recordError(e);
            return properties.getHeartbeatPeriod();
        }
        resetError();

        int period = getPeriod(response);
        if (nowPeriod.get() != period) {
            nowPeriod.set(period);
            logger.info("Receive heartbeat response period changes, set period to {}.", period);
        }
        if (period <= 0) {
            throw new IllegalStateException("Not has a legal period.");
        }
        return nowPeriod.get();
    }


    private int getPeriod(HeartbeatResponse response) {
        if (response.hasPeriod()) {
            return response.getPeriod();
        }
        int now = nowPeriod.get();
        if (now > 0) {
            return now;
        }
        return properties.getHeartbeatPeriod();
    }

    public void stop() {
        if (!start.get()) {
            return;
        }
        start.set(false);
        scheduledExecutorService.shutdown();
    }

    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    // log methods

    private void recordError(Exception e) {
        Status status = tryGetStatus(e);
        if (status != null) {
            recordStatus(status);
            return;
        }
        logger.error("Send heartbeat error, will retry for next time." +
                " Detail message will be print in trace level. message: {}.", e.getMessage());
        logger.trace("Send heartbeat error details message.", e);
    }

    private void resetError() {
        if (errorCount.get() == 0) {
            return;
        }
        logger.debug("Send heartbeat success after {} times.", errorCount.get());
        errorCount.set(0);
    }

    private void recordStatus(Status status) {
        if (status.isOk()) {
            return;
        }
        int count = errorCount.getAndIncrement();
        if (count != 0) {
            return;
        }
        logger.debug("Send heartbeat error, will retry for next time. Code: {}", status.getCode());
    }

    private Status tryGetStatus(Exception e) {
        return Status.fromThrowable(e);
    }
}
