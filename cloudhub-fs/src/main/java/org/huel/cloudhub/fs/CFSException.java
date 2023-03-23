package org.huel.cloudhub.fs;

import java.io.IOException;

/**
 * @author RollW
 */
public class CFSException extends IOException {
    public CFSException() {
        super();
    }

    public CFSException(String message) {
        super(message);
    }

    public CFSException(String message, Throwable cause) {
        super(message, cause);
    }

    public CFSException(Throwable cause) {
        super(cause);
    }
}
