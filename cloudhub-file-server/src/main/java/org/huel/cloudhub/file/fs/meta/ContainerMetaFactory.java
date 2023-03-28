package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.container.ContainerType;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public class ContainerMetaFactory {
    // TODO:
    public ContainerMetaBuilder createContainerMetaBuilder(ContainerType containerType) {
        return switch (containerType) {
            case ORIGINAL -> new RawContainerMeta.Builder();
            case REPLICA -> new ReplicaContainerMeta.Builder();
        };
    }

    public ContainerGroupMetaBuilder createContainerGroupMetaBuilder(
            ContainerType containerType) {
        return null;
    }

    public ContainerMeta loadContainerMeta(ServerFile metaFile) throws IOException {
        try (InputStream inputStream = metaFile.openInput()) {

        }
        return null;
    }
}
