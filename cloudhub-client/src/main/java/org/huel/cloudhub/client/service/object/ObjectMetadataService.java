package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.common.MessagePackage;

import java.util.Map;

/**
 * @author RollW
 */
public interface ObjectMetadataService {
    MessagePackage<Void> addObjectMetadata(String bucketName, String objectName,
                                           Map<String, String> metadata);

    Map<String, String> getObjectMetadata(String bucketName, String objectName);

}
