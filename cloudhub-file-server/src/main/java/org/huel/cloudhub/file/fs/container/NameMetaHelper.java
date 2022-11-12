package org.huel.cloudhub.file.fs.container;

/**
 * @author RollW
 */
public final class NameMetaHelper {

    public static boolean isReplicaContainer(String fileName) {
        return !ContainerNameMeta.check(fileName);
    }

    private NameMetaHelper() {
    }
}
