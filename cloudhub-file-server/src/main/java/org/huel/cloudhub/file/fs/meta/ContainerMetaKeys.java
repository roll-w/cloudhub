package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.server.service.file.FileUtils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author RollW
 */
public final class ContainerMetaKeys {
    public static final String CONTAINER_META_SUFFIX = ".cmeta";

    // contains no dot(.)
    private static final String CONTAINER_META_SUFFIX_N = "cmeta";

    public static final String REPLICA_CONTAINER_META_SUFFIX = ".rcmeta";

    private static final String REPLICA_CONTAINER_META_SUFFIX_N = "rcmeta";

    public static boolean isMetaFile(String fileName) {
        if (fileName == null) {
            throw new NullPointerException();
        }
        final String extName = FileUtils.getExtensionName(fileName);
        if (Objects.equals(extName, CONTAINER_META_SUFFIX_N)) {
            return true;
        }
        return Objects.equals(extName, REPLICA_CONTAINER_META_SUFFIX_N);
    }

    public static boolean isReplicaMetaFile(String metaFileName) {
        if (metaFileName == null) {
            throw new NullPointerException();
        }
        return metaFileName.contains(Pattern.quote("_"));
    }

    private ContainerMetaKeys() {
    }
}
