package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.container.ContainerNameMeta;
import org.huel.cloudhub.file.fs.container.ContainerType;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
@SuppressWarnings("unused")
public class ContainerMetaFactory {
    public ContainerMetaBuilder createContainerMetaBuilder(ContainerType containerType) {
        return switch (containerType) {
            case ORIGINAL -> new LocalContainerMeta.Builder();
            case REPLICA -> new ReplicaContainerMeta.Builder();
        };
    }

    public ContainerGroupMetaBuilder createContainerGroupMetaBuilder(
            ContainerType containerType) {
        return null;
    }

    private record ContainerNameInfo(
            String name,
            long serial,
            String source) {
    }

    public ContainerGroupMeta loadContainerGroupMeta(ServerFile metaFile)
            throws IOException {
        boolean isReplica = ContainerMetaKeys.isReplicaMetaFile(metaFile.getName());
        try (InputStream inputStream = metaFile.openInput()) {
            if (isReplica) {
                return new ReplicaContainerGroupMeta(
                        SerializedReplicaContainerGroupMeta.parseFrom(inputStream)
                );
            }
            return new LocalContainerGroupMeta(
                    SerializedContainerGroupMeta.parseFrom(inputStream)
            );
        }
    }

    public ContainerMeta loadContainerMeta(ServerFile metaFile,
                                           ContainerLocator containerLocator) throws IOException, MetadataLostException {
        if (!metaFile.exists()) {
            throw new MetadataLostException("Container meta file lost: " + metaFile.getPath());
        }

        String locator = removeSuffix(metaFile.getName());
        boolean isReplica = isReplica(locator);
        ContainerNameInfo containerNameInfo = readNameMeta(locator, isReplica);

        try (InputStream inputStream = metaFile.openInput()) {
            SerializedContainerBlockMeta serializedContainerBlockMeta =
                    SerializedContainerBlockMeta.parseFrom(inputStream);

            if (isReplica) {
                return new ReplicaContainerMeta(
                        locator,
                        containerNameInfo.serial(),
                        containerLocator.getVersion(),
                        containerNameInfo.source(),
                        serializedContainerBlockMeta
                );
            }
            return new LocalContainerMeta(
                    locator, containerNameInfo.serial(),
                    containerLocator.getVersion(),
                    serializedContainerBlockMeta
            );
        }
    }

    private ContainerNameInfo readNameMeta(String name, boolean replica) {
        if (replica) {
            ReplicaContainerNameMeta replicaContainerNameMeta =
                    ReplicaContainerNameMeta.parse(name);
            return new ContainerNameInfo(
                    replicaContainerNameMeta.getName(),
                    replicaContainerNameMeta.getSerial(),
                    replicaContainerNameMeta.getSourceId()
            );
        }
        ContainerNameMeta containerNameMeta = ContainerNameMeta.parse(name);
        return new ContainerNameInfo(
                containerNameMeta.getName(),
                containerNameMeta.getSerial(),
                null
        );
    }


    private String removeSuffix(String name) {
        return name.substring(0, name.lastIndexOf('.'));
    }

    private boolean isReplica(String name) {
        return ContainerMetaKeys.isReplicaMetaFile(name);
    }
}
