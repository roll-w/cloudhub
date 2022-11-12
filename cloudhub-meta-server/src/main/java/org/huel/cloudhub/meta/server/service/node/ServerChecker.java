package org.huel.cloudhub.meta.server.service.node;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

/**
 * @author RollW
 */
public interface ServerChecker {
    Collection<NodeServer> getActiveServers();

    Collection<NodeServer> getDeadServers();

    int getActiveServerCount();

    boolean isActive(@Nullable NodeServer nodeServer);

    boolean isActive(@Nullable String serverId);
}