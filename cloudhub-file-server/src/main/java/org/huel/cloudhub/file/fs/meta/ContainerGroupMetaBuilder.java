package org.huel.cloudhub.file.fs.meta;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerGroupMetaBuilder {
    // TODO: refactor container meta
    ContainerGroupMetaBuilder setLocators(List<? extends ContainerLocator> locators);

    ContainerGroupMeta build();
}
