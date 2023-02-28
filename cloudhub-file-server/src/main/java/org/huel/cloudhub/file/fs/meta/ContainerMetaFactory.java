package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerType;

/**
 * @author RollW
 */
public class ContainerMetaFactory {
    // TODO:
    public ContainerMetaBuilder createContainerMetaBuilder(ContainerType containerType) {
        return switch (containerType) {
            case ORIGINAL -> null;
            case REPLICA -> new ReplicaContainerMeta.Builder();
        };
    }

    public ContainerGroupMetaBuilder createContainerGroupMetaBuilder(ContainerType containerType) {
        return null;
    }
}
