package org.huel.cloudhub.meta.server.service.node;

import java.util.Collection;

/**
 * @author RollW
 */
public interface ServerChecker {
    Collection<NodeServer> getActiveServers();

    Collection<NodeServer> getDeadServers();

    boolean isActive(NodeServer nodeServer);
}