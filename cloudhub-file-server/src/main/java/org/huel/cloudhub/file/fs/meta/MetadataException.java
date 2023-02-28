package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public class MetadataException extends Exception {
    public MetadataException() {
        super();
    }

    public MetadataException(String message) {
        super(message);
    }

    public MetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataException(Throwable cause) {
        super(cause);
    }

    protected MetadataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
