package org.huel.cloudhub.file.io;

import java.io.IOException;

/**
 * @author RollW
 */
public class ClosedStreamException extends IOException {
    public ClosedStreamException() {
        super();
    }

    public ClosedStreamException(String message) {
        super(message);
    }

    public ClosedStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClosedStreamException(Throwable cause) {
        super(cause);
    }
}
