package org.huel.cloudhub.client.data.dto.fs;

import java.util.List;

/**
 * @author RollW
 */
public record ConnectedServers(
        List<ServerInfo> activeServers,
        List<ServerInfo> deadServers
) {
}
