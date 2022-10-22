package org.huel.cloudhub.file.server;

import org.huel.cloudhub.file.server.heartbeat.HeartbeatSendService;
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
    public FileServerApplication(HeartbeatSendService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

    private final HeartbeatSendService service;

    @PostConstruct
    public void sendHeartbeat() throws Exception {
        while (true) {
            Thread.sleep(1000);
            service.sendHeartbeat();
        }
    }
}
