package org.huel.cloudhub.file.server.heartbeat;

import org.springframework.stereotype.Component;

/**
 * @author RollW
 */
@Component
public class HeartbeatTask {
    private final HeartbeatSendService service;

    public HeartbeatTask(HeartbeatSendService service) {
        this.service = service;
    }
}
