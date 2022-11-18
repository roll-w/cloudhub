package org.huel.cloudhub.file.fs.container.replica;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ReplicaContainerDeleter {
    void deleteReplicaContainer(String id, long serial, String source) throws IOException;
}
