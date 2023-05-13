package org.huel.cloudhub.client.disk.domain.userstorage.util;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * @author RollW
 */
public class StorageNameValidator {

    public static String validate(String name) {
        if (name == null || name.isBlank()) {
            throw new StorageException(StorageErrorCode.ERROR_NAME_EMPTY);
        }
        if (name.length() > 120) {
            throw new StorageException(StorageErrorCode.ERROR_NAME_TOO_LONG);
        }
        return name.trim();
    }


    private StorageNameValidator() {
    }
}
