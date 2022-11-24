package org.huel.cloudhub.client.event.bucket;

import org.huel.cloudhub.client.event.object.OnObjectDeleteEvent;
import org.huel.cloudhub.client.service.object.ObjectRemoveHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author RollW
 */
@Component
public class OnBucketOrObjectDeleteEventListener implements ApplicationListener<OnBucketDeleteEvent> {
    private final List<ObjectRemoveHandler> objectRemoveHandlers;

    public OnBucketOrObjectDeleteEventListener(List<ObjectRemoveHandler> objectRemoveHandlers) {
        this.objectRemoveHandlers = objectRemoveHandlers;
    }

    @Override
    @Async
    public void onApplicationEvent(OnBucketDeleteEvent event) {
        String bucketName = event.getBucketInfo().name();
        objectRemoveHandlers.forEach(handler ->
                handler.handleBucketDelete(bucketName));
    }

    @EventListener
    @Async
    public void onObjectDelete(OnObjectDeleteEvent objectDeleteEvent) {
        objectRemoveHandlers.forEach(handler ->
                handler.handleObjectRemove(objectDeleteEvent.getObjectInfoDto()));
    }
}
