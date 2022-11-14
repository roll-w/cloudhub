package org.huel.cloudhub.file.server.service;

/**
 * @author RollW
 */
public interface SourceServerGetter {
    Server getLocalServer();

    record Server(String id, String host, int port) {
    }
}
