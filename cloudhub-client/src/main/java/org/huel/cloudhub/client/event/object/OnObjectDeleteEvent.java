package org.huel.cloudhub.client.event.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.springframework.context.ApplicationEvent;

/**
 * @author RollW
 */
public class OnObjectDeleteEvent extends ApplicationEvent {
    private final ObjectInfoDto objectInfoDto;

    public OnObjectDeleteEvent(ObjectInfoDto objectInfoDto) {
        super(objectInfoDto);
        this.objectInfoDto = objectInfoDto;
    }

    public ObjectInfoDto getObjectInfoDto() {
        return objectInfoDto;
    }
}
