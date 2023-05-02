package org.huel.cloudhub.client.disk.domain.storagepermission;

/**
 * @author RollW
 */
public enum PublicPermissionType {
    PUBLIC_READ(true, false),
    PUBLIC_READ_WRITE(true, true),
    PRIVATE(false, false);

    private final boolean read;
    private final boolean write;

    PublicPermissionType(boolean read, boolean write) {
        this.read = read;
        this.write = write;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isWrite() {
        return write;
    }

}
