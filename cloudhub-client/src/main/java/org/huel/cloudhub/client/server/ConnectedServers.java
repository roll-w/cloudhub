package org.huel.cloudhub.client.server;

import java.util.List;

/**
 * @author RollW
 */
public record ConnectedServers(
        List<FileServerLocation> activeServers,
        List<FileServerLocation> deadServers
) {
}
