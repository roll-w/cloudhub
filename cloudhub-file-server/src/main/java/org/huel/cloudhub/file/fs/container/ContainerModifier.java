package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.io.SeekableOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Container 修改创建接口。
 *
 * @author RollW
 */
public interface ContainerModifier extends ContainerCreator {
    SeekableOutputStream openContainerWrite(Container container) throws IOException, LockException;

    void closeContainerWrite(Container container, OutputStream stream);

    @Override
    void createsContainerFileWithMeta(Container container) throws IOException;

    @Override
    void updatesContainerMetadata(Container container) throws MetaException, IOException;
}
