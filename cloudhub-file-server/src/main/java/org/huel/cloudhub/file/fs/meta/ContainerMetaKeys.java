package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerIdentity;
import org.huel.cloudhub.file.fs.container.ContainerNameMeta;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.huel.cloudhub.file.server.service.file.FileUtils;

/**
 * @author RollW
 */
public final class ContainerMetaKeys {
    public static final String CONTAINER_META_SUFFIX = ".cmeta";

    // contains no dot(.)
    private static final String CONTAINER_META_SUFFIX_N = "cmeta";

    public static final String REPLICA_CONTAINER_META_SUFFIX = ".rcmeta";

    private static final String REPLICA_CONTAINER_META_SUFFIX_N = "rcmeta";

    public static String toMetaFileName(String containerLocator) {
        if (containerLocator == null) {
            throw new NullPointerException();
        }
        if (ReplicaContainerNameMeta.check(containerLocator)) {
            ReplicaContainerNameMeta containerNameMeta =
                    ReplicaContainerNameMeta.parse(containerLocator);
            return ContainerIdentity.toCmetaId(containerNameMeta.getId()) + REPLICA_CONTAINER_META_SUFFIX;
        }
        ContainerNameMeta containerNameMeta = ContainerNameMeta.parse(containerLocator);
        return ContainerIdentity.toCmetaId(containerNameMeta.getId()) + CONTAINER_META_SUFFIX;
    }

    public static boolean isMetaFile(String fileName) {
        if (fileName == null) {
            throw new NullPointerException();
        }
        final String extName = FileUtils.getExtensionName(fileName);
        if (CONTAINER_META_SUFFIX_N.equalsIgnoreCase(extName)) {
            return true;
        }
        return REPLICA_CONTAINER_META_SUFFIX_N.equalsIgnoreCase(extName);
    }

    public static boolean isReplicaMetaFile(String metaFileName) {
        if (metaFileName == null) {
            throw new NullPointerException();
        }
        return metaFileName.contains("_");
    }

    private ContainerMetaKeys() {
    }
}
