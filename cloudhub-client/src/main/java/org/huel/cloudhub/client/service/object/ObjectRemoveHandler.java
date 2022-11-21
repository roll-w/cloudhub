package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;

import java.util.List;

/**
 * @author RollW
 */
public interface ObjectRemoveHandler {
    void handle(ObjectInfo objectInfo);

    void handle(List<ObjectInfo> objectInfos);

    void handleBucketDelete(String bucketName);
}
