package org.huel.cloudhub.client.event.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;

/**
 * @author RollW
 */
public interface ObjectRequestEvent {
    ObjectInfo getObjectInfo();
}
