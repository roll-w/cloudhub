package org.huel.cloudhub.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * @author RollW
 */
@ConfigurationProperties("cloudhub.grpc")
public class GrpcProperties {
    private final int port;

    @ConstructorBinding
    public GrpcProperties(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
