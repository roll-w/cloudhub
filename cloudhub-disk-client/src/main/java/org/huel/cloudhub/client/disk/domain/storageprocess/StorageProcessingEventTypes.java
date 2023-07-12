package org.huel.cloudhub.client.disk.domain.storageprocess;

import java.util.Set;

/**
 * @author RollW
 */
public record StorageProcessingEventTypes(
        Set<StorageProcessingEventType> storageProcessingEventTypes
) {
    public boolean contains(StorageProcessingEventType storageProcessingEventType) {
        return storageProcessingEventTypes.contains(storageProcessingEventType);
    }

    public String toString() {
        return storageProcessingEventTypes.toString();
    }

    public static StorageProcessingEventTypes of(
            StorageProcessingEventType... storageProcessingEventTypes) {
        return new StorageProcessingEventTypes(
                Set.of(storageProcessingEventTypes)
        );
    }
}
