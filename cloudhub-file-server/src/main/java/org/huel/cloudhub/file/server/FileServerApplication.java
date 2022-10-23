package org.huel.cloudhub.file.server;

import org.huel.cloudhub.file.server.heartbeat.HeartbeatTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.PostConstruct;

/**
 * File server start class.
 *
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FileServerApplication {

    public FileServerApplication(HeartbeatTask heartbeatTask) {
        this.heartbeatTask = heartbeatTask;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

    private final HeartbeatTask heartbeatTask;

    @PostConstruct
    public void sendHeartbeat() {
        heartbeatTask.startSendHeartbeat();
    }
}
