package org.huel.cloudhub.client.event.bucket;

import org.huel.cloudhub.client.service.object.ObjectRemoveHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
    public void onApplicationEvent(OnBucketDeleteEvent event) {
        String bucketName = event.getBucketInfo().name();
        objectRemoveHandlers.forEach(handler ->
                handler.handleBucketDelete(bucketName));
    }
}
