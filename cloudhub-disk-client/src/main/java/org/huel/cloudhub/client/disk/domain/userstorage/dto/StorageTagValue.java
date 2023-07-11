package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.tag.NameValue;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;

/**
 * @author RollW
 */
public record StorageTagValue(
        long id,
        long tagGroupId,
        long tagId,
        String name,
        String value
) {

    public static final long INVALID_ID = -1;

    public static StorageTagValue of(NameValue nameValue) {
        return new StorageTagValue(
                INVALID_ID,
                INVALID_ID,
                INVALID_ID,
                nameValue.name(),
                nameValue.value()
        );
    }

    public static StorageTagValue of(String name, String value) {
        return new StorageTagValue(
                INVALID_ID,
                INVALID_ID,
                INVALID_ID,
                name, value
        );
    }

    public static StorageTagValue of(StorageMetadata storageMetadata,
                                 String name, String value) {
        if (storageMetadata == null) {
            return null;
        }
        return new StorageTagValue(
                storageMetadata.getId(),
                storageMetadata.getTagGroupId(),
                storageMetadata.getTagId(),
                name, value
        );
    }
}
