package org.huel.cloudhub.client.event.object;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author RollW
 */
@Component
public class OnObjectRenameEventListener implements ApplicationListener<OnObjectRenameEvent> {
    @Override
    @Async
    public void onApplicationEvent(OnObjectRenameEvent event) {
    }
}
