package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author RollW
 */
@Service
public class ObjectMetadataServiceImpl implements ObjectMetadataService {
    @Override
    public MessagePackage<Void> setObjectMetadata(String bucketName, String objectName, Map<String, String> metadata) {
        return null;
    }

    @Override
    public Map<String, String> getObjectMetadata(String bucketName, String objectName) {
        return null;
    }
}
