package org.huel.cloudhub.file.server;

import org.huel.cloudhub.file.server.heartbeat.HeartbeatSendService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * File server start class.
 *
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FileServerApplication implements ApplicationRunner {
    public FileServerApplication(HeartbeatSendService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

    private final HeartbeatSendService service;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            Thread.sleep(999);
            service.sendHeartbeat();
        }
    }
}
