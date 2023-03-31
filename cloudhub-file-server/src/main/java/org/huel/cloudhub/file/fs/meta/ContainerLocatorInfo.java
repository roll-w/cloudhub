package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public record ContainerLocatorInfo(
        String locator,
        String id,
        long serial,
        long version,
        String source
) implements ContainerLocator {
    @Override
    public String getLocator() {
        return locator;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getSerial() {
        return serial;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public String getSource() {
        return source;
    }
}
