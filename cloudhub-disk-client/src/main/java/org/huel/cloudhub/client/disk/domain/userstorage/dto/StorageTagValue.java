package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.tag.dto.TagValue;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;

/**
 * @author RollW
 */
public record StorageTagValue(
        long id,
        long tagId,
        String name,
        String value
) {

    public static final long INVALID_ID = -1;

    public static StorageTagValue of(TagValue tagValue) {
        return new StorageTagValue(
                INVALID_ID,
                INVALID_ID,
                tagValue.name(),
                tagValue.value()
        );
    }

    public static StorageTagValue of(String name, String value) {
        return new StorageTagValue(
                INVALID_ID,
                INVALID_ID,
                name, value
        );
    }

    public static StorageTagValue of(StorageMetadata storageMetadata) {
        if (storageMetadata == null) {
            return null;
        }
        return new StorageTagValue(
                storageMetadata.getId(),
                storageMetadata.getTagId(),
                storageMetadata.getName(),
                storageMetadata.getValue()
        );
    }
}
