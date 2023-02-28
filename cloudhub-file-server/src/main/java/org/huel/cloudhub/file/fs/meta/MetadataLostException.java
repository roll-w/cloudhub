package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public class MetadataLostException extends MetadataException {
    private final String containerLocator;

    public MetadataLostException(String containerLocator) {
        super("Metadata lost: " + containerLocator);
        this.containerLocator = containerLocator;
    }

    public String getContainerLocator() {
        return containerLocator;
    }
}
