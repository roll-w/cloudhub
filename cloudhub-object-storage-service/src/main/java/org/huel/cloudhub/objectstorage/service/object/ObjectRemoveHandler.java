package org.huel.cloudhub.objectstorage.service.object;

import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoDto;

import java.util.List;

/**
 * @author RollW
 */
public interface ObjectRemoveHandler {
    void handleObjectRemove(ObjectInfoDto objectInfoDto);

    void handleObjectRemove(List<ObjectInfoDto> objectInfoDtos);

    void handleBucketDelete(String bucketName);
}
