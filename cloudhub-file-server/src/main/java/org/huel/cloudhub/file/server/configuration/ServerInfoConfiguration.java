package org.huel.cloudhub.file.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author RollW
 */
@Configuration
public class ServerInfoConfiguration {

    @Bean
    public InetAddress localhostInetAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
}