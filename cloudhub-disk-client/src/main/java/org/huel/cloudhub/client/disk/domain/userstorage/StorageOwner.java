package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;

/**
 * @author RollW
 */
public interface StorageOwner {
    long getOwnerId();

    LegalUserType getOwnerType();
}
