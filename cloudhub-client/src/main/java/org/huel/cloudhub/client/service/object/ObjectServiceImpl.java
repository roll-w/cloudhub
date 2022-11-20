package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.database.repository.FileObjectStorageRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import org.huel.cloudhub.client.service.bucket.BucketService;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author RollW
 */
@Service
public class ObjectServiceImpl implements ObjectService {
    private final BucketService bucketService;
    private final FileObjectStorageRepository repository;

    public ObjectServiceImpl(BucketService bucketService,
                             FileObjectStorageRepository repository) {
        this.bucketService = bucketService;
        this.repository = repository;
    }

    @Override
    public MessagePackage<ObjectInfo> saveObject(InputStream stream) {
        return null;
    }

    @Override
    public MessagePackage<Void> getObjectData(String bucketName, String objectName,
                                              OutputStream stream, Long userId) {
        Bucket bucket = bucketService.getBucketByName(bucketName);
        if (bucket == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist", null);
        }
        FileObjectStorage storage = repository.getById(bucketName, objectName);
        if (storage == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Object not exist", null);
        }
        if (bucket.getBucketVisibility().isNeedReadAuth() &&
                !authUserId(userId, bucket.getUserId())) {
            return new MessagePackage<>(ErrorCode.ERROR_PERMISSION_NOT_ALLOWED,
                    "You have no permissions.", null);
        }

        // write
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    private boolean authUserId(Long userId, long expect) {
        if (userId == null) {
            return false;
        }
        return userId == expect;
    }

    private void writeTo(OutputStream stream, String fileId) {

    }
}
