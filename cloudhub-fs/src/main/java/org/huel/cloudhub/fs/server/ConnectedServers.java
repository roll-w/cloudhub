package org.huel.cloudhub.fs.server;

import java.util.List;

/**
 * @author RollW
 */
public record ConnectedServers(
        List<ServerInfo> activeServers,
        List<ServerInfo> deadServers
) {
}
