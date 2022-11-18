package org.huel.cloudhub.file.fs.meta;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerGroupMetaBuilder {
    // TODO: refactor container meta
    ContainerGroupMetaBuilder setMetas(List<? extends ContainerMeta> metas);

    ContainerGroupMeta build();
}
