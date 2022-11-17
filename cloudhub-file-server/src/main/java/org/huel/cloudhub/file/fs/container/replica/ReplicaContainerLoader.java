package org.huel.cloudhub.file.fs.container.replica;

import org.huel.cloudhub.file.fs.meta.SerializedReplicaContainerGroupMeta;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ReplicaContainerLoader {
    void loadInReplicaContainers(SerializedReplicaContainerGroupMeta containerGroupMeta) throws IOException;
}
