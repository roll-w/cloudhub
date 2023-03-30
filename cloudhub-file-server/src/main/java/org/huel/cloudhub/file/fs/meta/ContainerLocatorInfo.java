package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public record ContainerLocatorInfo(
        String locator,
        long serial,
        long version,
        String source
) implements ContainerLocator {
    @Override
    public String getLocator() {
        return locator;
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
