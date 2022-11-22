package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;

import java.util.List;

/**
 * @author RollW
 */
public interface ObjectRemoveHandler {
    void handleObjectRemove(ObjectInfoDto objectInfoDto);

    void handleObjectRemove(List<ObjectInfoDto> objectInfoDtos);

    void handleBucketDelete(String bucketName);
}
