package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public interface ContainerMetaBuilder {
    ContainerMetaBuilder setVersion(long version);

    ContainerMetaBuilder setLocator(String locator);

    ContainerMeta build();
}
