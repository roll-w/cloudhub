package org.huel.cloudhub.client.service.rpc;

/**
 * @author RollW
 */
public class FileDownloadingException extends RuntimeException {
    private final Type type;

    public FileDownloadingException(Type type) {
        this.type = type;
    }

    public FileDownloadingException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SERVER_DOWN,
        NOT_EXIST,
        DATA_LOSS
    }
}
