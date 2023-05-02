package org.huel.cloudhub.client.disk.domain.operatelog;

/**
 * @author RollW
 */
public enum Action {
    CREATE,
    UPDATE,
    ACCESS(false),
    EDIT,
    DELETE,
    MOVE,
    COPY,
    RENAME,
    ;

    private final boolean isWrite;

    Action(boolean isWrite) {
        this.isWrite = isWrite;
    }

    Action() {
        this(true);
    }

    public boolean isWrite() {
        return isWrite;
    }

    public boolean isRead() {
        return !isWrite();
    }
}
