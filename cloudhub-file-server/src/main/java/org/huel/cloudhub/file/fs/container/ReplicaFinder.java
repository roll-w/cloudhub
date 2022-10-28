package org.huel.cloudhub.file.fs.container;

/**
 * @author RollW
 */
public interface ReplicaFinder {
    ReplicaContainer findReplica(String sourceServerId, String containerId, long serial);
}
