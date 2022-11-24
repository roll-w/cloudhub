package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.common.MessagePackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ObjectService {
    MessagePackage<ObjectInfoDto> saveObject(ObjectInfo objectInfo, InputStream stream) throws IOException;

    void getObjectData(ObjectInfo objectInfo, OutputStream stream);

    MessagePackage<Void> deleteObject(ObjectInfo objectInfo);

    MessagePackage<Void> clearBucketObjects(String bucketName);

    List<ObjectInfoDto> getObjectsInBucket(String bucketName);

    MessagePackage<ObjectInfoDto> renameObject(ObjectInfo oldInfo, String newName);

    boolean isObjectExist(ObjectInfo objectInfo);
}
