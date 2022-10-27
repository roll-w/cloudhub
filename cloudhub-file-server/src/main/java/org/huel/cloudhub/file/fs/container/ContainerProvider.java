package org.huel.cloudhub.file.fs.container;

import java.io.InputStream;

/**
 * @author RollW
 */
public interface ContainerProvider {
    default InputStream openContainer(Container container) {
        return openContainer(container.getLocation());
    }

    InputStream openContainer(ContainerLocation location);
}
