package org.huel.cloudhub.objectstorage.service.object;

import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfo;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoDto;

/**
 * @author RollW
 */
public interface ObjectChangeActionHandler {
    void onAddNewObject(ObjectInfoDto objectInfoDto);

    void onObjectRename(ObjectInfo oldInfo, String newName);
}
