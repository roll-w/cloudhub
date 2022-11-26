package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.common.MessagePackage;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public interface ObjectMetadataService {
    MessagePackage<Void> addObjectMetadataWithCheck(String bucketName, String objectName,
                                           Map<String, String> metadata);

    MessagePackage<Void> addObjectMetadata(String bucketName, String objectName,
                                           Map<String, String> metadata);

    Map<String, String> getObjectMetadata(String bucketName, String objectName);

    MessagePackage<Void> removeObjectMetadata(String bucketName, String objectName,
                                              List<String> metadataKeys);
}
