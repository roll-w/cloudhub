package org.huel.cloudhub.file.io;

import java.io.IOException;

/**
 * @author RollW
 */
public class ReachLimitException extends IOException {
    public ReachLimitException() {
        super();
    }

    public ReachLimitException(String message) {
        super(message);
    }

    public ReachLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReachLimitException(Throwable cause) {
        super(cause);
    }
}
