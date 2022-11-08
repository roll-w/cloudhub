package org.huel.cloudhub.file.server.configuration;

import org.huel.cloudhub.file.fs.LocalFileServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
public class FileConfiguration {

    @Bean
    public LocalFileServer localFileServer() {
        return new LocalFileServer();
    }


}
