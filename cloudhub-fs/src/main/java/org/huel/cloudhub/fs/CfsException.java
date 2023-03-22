package org.huel.cloudhub.fs;

import java.io.IOException;

/**
 * @author RollW
 */
public class CfsException extends IOException {
    public CfsException() {
        super();
    }

    public CfsException(String message) {
        super(message);
    }

    public CfsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CfsException(Throwable cause) {
        super(cause);
    }
}
