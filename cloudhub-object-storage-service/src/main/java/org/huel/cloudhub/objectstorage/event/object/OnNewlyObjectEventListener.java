package org.huel.cloudhub.objectstorage.event.object;

import org.huel.cloudhub.objectstorage.service.object.ObjectChangeActionHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author RollW
 */
@Component
public class OnNewlyObjectEventListener implements ApplicationListener<OnNewlyObjectEvent> {
    private final List<ObjectChangeActionHandler> handlers;

    public OnNewlyObjectEventListener(List<ObjectChangeActionHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    @Async
    public void onApplicationEvent(OnNewlyObjectEvent event) {
        handlers.forEach(handler ->
                handler.onAddNewObject(event.getObjectInfoDto()));
    }
}
