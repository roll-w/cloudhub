package org.huel.cloudhub.client.event.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
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
