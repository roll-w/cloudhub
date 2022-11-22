package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;

/**
 * @author RollW
 */
public interface ObjectChangeActionHandler {
    void onAddNewObject(ObjectInfoDto objectInfoDto);

    void onObjectRename(ObjectInfo oldInfo, String newName);
}
