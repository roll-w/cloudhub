package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.meta.MetaException;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ContainerCreator {
    /**
     * 创建容器文件及其元数据文件。
     */
    void createsContainerFileWithMeta(Container container) throws IOException;

    void updatesContainerMetadata(Container container) throws MetaException, IOException;
}
