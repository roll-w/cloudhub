package org.huel.cloudhub.file.fs.container;

/**
 * @author RollW
 */
public enum ContainerType {
    ORIGINAL(ContainerLocation.META_SUFFIX),
    REPLICA(ContainerLocation.REPLICA_META_SUFFIX);

    private final String metaSuffix;

    ContainerType(String metaSuffix) {
        this.metaSuffix = metaSuffix;
    }

    public String getMetaSuffix() {
        return metaSuffix;
    }
}
