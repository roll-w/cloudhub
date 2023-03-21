package org.huel.cloudhub.client.service.object;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public interface ObjectMetadataService {
    void addObjectMetadataWithCheck(String bucketName, String objectName,
                                    Map<String, String> metadata);

    void addObjectMetadata(String bucketName, String objectName,
                           Map<String, String> metadata);

    Map<String, String> getObjectMetadata(String bucketName, String objectName);

    void removeObjectMetadata(String bucketName, String objectName,
                              List<String> metadataKeys);
}
