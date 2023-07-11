package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.tag.NameValue;

import java.util.List;

/**
 * @author RollW
 */
public interface StorageCategoryService {
    List<? extends AttributedStorage> getByType(
            StorageOwner storageOwner,
            FileType fileType);

    List<? extends AttributedStorage> getByTags(
            StorageOwner storageOwner,
            List<NameValue> nameValues);

    List<? extends AttributedStorage> getByTypeAndTags(
            StorageOwner storageOwner,
            FileType fileType, List<NameValue> nameValues);
}
