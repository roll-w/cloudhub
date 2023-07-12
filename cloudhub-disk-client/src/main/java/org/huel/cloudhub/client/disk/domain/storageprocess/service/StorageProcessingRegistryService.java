package org.huel.cloudhub.client.disk.domain.storageprocess.service;

import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEventRegistry;
import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEventTypes;
import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEvent;
import org.huel.cloudhub.client.disk.event.EventCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class StorageProcessingRegistryService
        implements StorageProcessingEventRegistry, StorageProcessingCallback {
    private static final Logger logger = LoggerFactory.getLogger(StorageProcessingRegistryService.class);

    private final Map<String, Integer> idToIndex = new HashMap<>();
    private final Map<String, Callback> callbacks
            = new HashMap<>();

    public StorageProcessingRegistryService() {
    }

    @Override
    public String register(EventCallback<StorageProcessingEvent> eventCallback,
                           StorageProcessingEventTypes messagePattern) {
        String id = toId(eventCallback);
        if (callbacks.containsKey(id)) {
            logger.error("EventCallback {} already registered, it should not happen.", id);
        }
        callbacks.put(id, new Callback(eventCallback, messagePattern));
        return id;
    }

    private String toId(EventCallback<StorageProcessingEvent> eventCallback) {
        String name = eventCallback.getClass().getSimpleName();
        Integer index = idToIndex.getOrDefault(name, 0) + 1;
        idToIndex.put(name, index);
        return name + "-" + index;
    }

    @Override
    public void unregister(String eventId) {
        callbacks.remove(eventId);
    }

    @Override
    public void onProcessed(StorageProcessingEvent storageProcessingEvent) {
        callbacks.forEach((id, callback) -> {
            if (callback.messagePattern().contains(
                    storageProcessingEvent.getStorageProcessEventType())) {
                callback.eventCallback().onEvent(storageProcessingEvent);
            }
        });
    }

    private record Callback(
            EventCallback<StorageProcessingEvent> eventCallback,
            StorageProcessingEventTypes messagePattern
    ) {
    }
}
