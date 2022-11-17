package org.huel.cloudhub.file.fs.container.replica;

import org.huel.cloudhub.file.fs.container.ContainerGroup;

/**
 * @author RollW
 */
public interface ReplicaContainerFinder {
    ContainerGroup findContainerGroup(String containerId, String source);
}
