package org.huel.cloudhub.file.fs;

/**
 * Lock exception.
 *
 * @author RollW
 */
public class LockException extends Exception {
    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, InterruptedException cause) {
        super(message, cause);
    }

    public LockException(InterruptedException cause) {
        this("Cannot get lock.", cause);
    }
}
