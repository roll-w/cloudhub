package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageTagValue;

import java.util.List;

/**
 * @author RollW
 */
public interface StorageAttributesService {
    List<StorageTagValue> getStorageTags(
            StorageIdentity storageIdentity, StorageOwner storageOwner);


}
