package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public class MetaException extends Exception {
    public MetaException() {
        super();
    }

    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetaException(Throwable cause) {
        super(cause);
    }

    protected MetaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
