package org.huel.cloudhub.file.server.service;

/**
 * @author RollW
 */
public class GrpcProperties {
    private final int port;
    private final int maxRequestSize;

    public GrpcProperties(int port, int maxRequestSize) {
        this.port = port;
        this.maxRequestSize = maxRequestSize;
    }

    public int getPort() {
        return port;
    }


    public int getMaxRequestSize() {
        return maxRequestSize;
    }

    public long getMaxRequestSizeBytes() {
        return maxRequestSize * 1024L * 1024L;
    }
}
