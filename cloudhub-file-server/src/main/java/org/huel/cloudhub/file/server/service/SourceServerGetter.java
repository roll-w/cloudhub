package org.huel.cloudhub.file.server.service;

/**
 * @author RollW
 */
public interface SourceServerGetter {
    ServerInfo getLocalServer();

    record ServerInfo(String id, String host, int port) {
    }
}
