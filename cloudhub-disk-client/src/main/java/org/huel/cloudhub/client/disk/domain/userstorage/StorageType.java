package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public enum StorageType implements SystemResourceKind.Kind {
    FILE(SystemResourceKind.FILE),
    FOLDER(SystemResourceKind.FOLDER),
    LINK(SystemResourceKind.LINK);

    private final SystemResourceKind systemResourceKind;

    StorageType(SystemResourceKind systemResourceKind) {
        this.systemResourceKind = systemResourceKind;
    }

    public static StorageType from(String nameIgnoreCase) {
        for (StorageType value : values()) {
            if (value.name().equalsIgnoreCase(nameIgnoreCase)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return systemResourceKind;
    }
}
