package org.huel.cloudhub.file.fs.container.replica;

import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.meta.SerializedContainerBlockMeta;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ReplicaContainerCreator {
    // will not create local files
    Container findOrCreateContainer(String id, String source, long serial,
                                    SerializedContainerBlockMeta serializedContainerBlockMeta);

    // will create local files, and will update if exists.
    void createContainerWithMeta(Container container,
                                 SerializedContainerBlockMeta serializedContainerBlockMeta) throws IOException;
}
