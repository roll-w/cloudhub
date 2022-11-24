package org.huel.cloudhub.client.event.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author RollW
 */
public class ObjectPutEvent extends ApplicationEvent implements ObjectRequestEvent {
    private final ObjectInfo objectInfo;

    public ObjectPutEvent(ObjectInfo objectInfo) {
        super(objectInfo);
        this.objectInfo = objectInfo;
    }

    @Override
    public ObjectInfo getObjectInfo() {
        return objectInfo;
    }
}
