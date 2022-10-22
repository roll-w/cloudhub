package org.huel.cloudhub.file.server.heartbeat;

import org.springframework.stereotype.Component;

/**
 * @author RollW
 */
@Component
public class HeartbeatTask {
    private final ServerHeartbeatService service;

    public HeartbeatTask(ServerHeartbeatService service) {
        this.service = service;
    }
}
