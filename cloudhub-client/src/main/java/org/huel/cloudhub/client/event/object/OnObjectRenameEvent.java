package org.huel.cloudhub.client.event.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author RollW
 */
public class OnObjectRenameEvent extends ApplicationEvent {
    private final ObjectInfo oldInfo;
    private final String newName;

    public OnObjectRenameEvent(ObjectInfo oldInfo, String newName) {
        super(oldInfo);
        this.oldInfo = oldInfo;
        this.newName = newName;
    }

    public ObjectInfo getOldInfo() {
        return oldInfo;
    }

    public String getNewName() {
        return newName;
    }
}
