package org.huel.cloudhub.file.fs.meta;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContainerGroupMeta {
    List<? extends ContainerLocator> getChildLocators();

    List<? extends ContainerMeta> loadChildContainerMeta(MetadataIndexLoader loader);

    String getSource();

    void writeTo(OutputStream outputStream) throws IOException;
}
