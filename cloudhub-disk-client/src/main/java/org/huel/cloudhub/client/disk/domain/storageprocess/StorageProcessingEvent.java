package org.huel.cloudhub.client.disk.domain.storageprocess;

import org.huel.cloudhub.client.disk.domain.tag.TaggedValue;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;

import java.util.List;

/**
 * @author RollW
 */
public class StorageProcessingEvent {
    private final StorageProcessingEventType storageProcessingEventType;
    private final AttributedStorage storage;
    private final long size;
    private final List<TaggedValue> taggedValues;

    public StorageProcessingEvent(StorageProcessingEventType storageProcessingEventType,
                                  AttributedStorage storage,
                                  long size,
                                  List<TaggedValue> taggedValues) {
        this.storageProcessingEventType = storageProcessingEventType;
        this.storage = storage;
        this.size = size;
        this.taggedValues = taggedValues;
    }

    public StorageProcessingEventType getStorageProcessEventType() {
        return storageProcessingEventType;
    }

    public AttributedStorage getStorage() {
        return storage;
    }

    public long getSize() {
        return size;
    }

    public List<TaggedValue> getTaggedValues() {
        return taggedValues;
    }
}
