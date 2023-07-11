package org.huel.cloudhub.client.disk.domain.storageprocess;

import org.huel.cloudhub.client.disk.event.EventRegistry;

/**
 * @author RollW
 */
public interface StorageProcessingEventRegistry extends
        EventRegistry<StorageProcessingEvent, StorageProcessingEventTypes> {
}
