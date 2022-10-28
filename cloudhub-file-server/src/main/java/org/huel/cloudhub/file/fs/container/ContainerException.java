package org.huel.cloudhub.file.fs.container;

import java.io.IOException;

/**
 * @author RollW
 */
public class ContainerException extends IOException {
    public ContainerException() {
        super();
    }

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }
}
