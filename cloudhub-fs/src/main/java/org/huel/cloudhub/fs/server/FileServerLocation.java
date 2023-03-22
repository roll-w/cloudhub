package org.huel.cloudhub.fs.server;

/**
 * @author RollW
 */
public record FileServerLocation(
        String host,
        int port,
        String serverId
) {
}
