package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public class MetaLostException extends MetaException {
    private final String containerLocator;

    public MetaLostException(String containerLocator) {
        super("Metadata lost: " + containerLocator);
        this.containerLocator = containerLocator;
    }

    public String getContainerLocator() {
        return containerLocator;
    }
}
