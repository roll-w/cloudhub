package org.huel.cloudhub.client.event.bucket;

import org.huel.cloudhub.client.service.object.ObjectRemoveHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.ref.Cleaner;
import java.util.List;

/**
 * @author RollW
 */
@Component
public class OnBucketDeleteEventListener implements ApplicationListener<OnBucketDeleteEvent> {
    private final List<ObjectRemoveHandler> objectRemoveHandlers;

    public OnBucketDeleteEventListener(List<ObjectRemoveHandler> objectRemoveHandlers) {
        this.objectRemoveHandlers = objectRemoveHandlers;
    }

    @Override
    @Async
    public void onApplicationEvent(OnBucketDeleteEvent event) {
        String bucketName = event.getBucketInfo().name();
        Cleaner cleaner = Cleaner.create();
        objectRemoveHandlers.forEach(handler ->
                handler.handleBucketDelete(bucketName));
    }
}
