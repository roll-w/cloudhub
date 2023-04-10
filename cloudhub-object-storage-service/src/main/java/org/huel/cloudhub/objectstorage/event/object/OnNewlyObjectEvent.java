package org.huel.cloudhub.objectstorage.event.object;

import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoDto;
import org.springframework.context.ApplicationEvent;

/**
 * @author RollW
 */
public class OnNewlyObjectEvent extends ApplicationEvent {
    private final ObjectInfoDto objectInfoDto;

    public OnNewlyObjectEvent(ObjectInfoDto source) {
        super(source);
        this.objectInfoDto = source;
    }

    public ObjectInfoDto getObjectInfoDto() {
        return objectInfoDto;
    }
}
