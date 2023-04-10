package org.huel.cloudhub.objectstorage.event.object;

import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfo;

/**
 * @author RollW
 */
public interface ObjectRequestEvent {
    ObjectInfo getObjectInfo();
}
