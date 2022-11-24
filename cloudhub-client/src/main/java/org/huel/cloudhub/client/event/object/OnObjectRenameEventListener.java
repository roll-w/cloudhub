package org.huel.cloudhub.client.event.object;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.client.service.object.ObjectChangeActionHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author RollW
 */
@Component
public class OnObjectRenameEventListener implements ApplicationListener<OnObjectRenameEvent> {
    private final List<ObjectChangeActionHandler> handlers;

    public OnObjectRenameEventListener(List<ObjectChangeActionHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    @Async
    public void onApplicationEvent(@NonNull OnObjectRenameEvent event) {
        handlers.forEach(handler ->
                handler.onObjectRename(event.getOldInfo(), event.getNewName()));
    }
}
