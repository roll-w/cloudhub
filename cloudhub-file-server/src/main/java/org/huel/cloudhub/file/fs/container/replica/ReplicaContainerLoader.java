package org.huel.cloudhub.file.fs.container.replica;

import org.huel.cloudhub.file.fs.meta.SerializedContainerGroupMeta;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ReplicaContainerLoader {
    void loadInReplicaContainers(SerializedContainerGroupMeta containerGroupMeta) throws IOException;
}
