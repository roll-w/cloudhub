package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.common.MessagePackage;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author RollW
 */
public interface ObjectService {
    MessagePackage<ObjectInfo> saveObject(InputStream stream);

    MessagePackage<Void> getObjectData(String bucketName, String objectName,
                                       OutputStream stream, Long userId);
}
