package org.huel.cloudhub.client.service.object;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.data.database.repository.FileObjectStorageRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.object.FileObjectStorage;
import org.huel.cloudhub.client.service.bucket.BucketService;
import org.huel.cloudhub.client.service.rpc.ClientFileDownloadService;
import org.huel.cloudhub.client.service.rpc.ClientFileUploadService;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author RollW
 */
@Service
public class ObjectServiceImpl implements ObjectService, ObjectRemoveHandler {
    private final BucketService bucketService;
    private final FileObjectStorageRepository repository;
    private final ClientFileDownloadService clientFileDownloadService;
    private final ClientFileUploadService clientFileUploadService;

    public ObjectServiceImpl(BucketService bucketService,
                             FileObjectStorageRepository repository,
                             ClientFileDownloadService clientFileDownloadService,
                             ClientFileUploadService clientFileUploadService) {
        this.bucketService = bucketService;
        this.repository = repository;
        this.clientFileDownloadService = clientFileDownloadService;
        this.clientFileUploadService = clientFileUploadService;
    }

    @Override
    public MessagePackage<ObjectInfo> saveObject(ObjectInfo objectInfo,
                                                 InputStream stream) throws IOException {
        validateObjectInfo(objectInfo);
        Bucket bucket = bucketService.getBucketByName(objectInfo.bucketName());
        if (bucket == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist", null);
        }
        var fileIdWrapper = new Object(){
            String fileId = null;
            long fileSize = -1;
        };

        CountDownLatch latch = new CountDownLatch(1);
        clientFileUploadService.uploadFile(stream, (success, fileId, fileSize) -> {
           latch.countDown();
           if (!success) {
               return;
           }
           fileIdWrapper.fileId = fileId;
           fileIdWrapper.fileSize = fileSize;
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (fileIdWrapper.fileId == null) {
            return new MessagePackage<>(ErrorCode.ERROR_FILE,
                    "Upload file error.", null);
        }
        FileObjectStorage storage = saveLocation(
                objectInfo.bucketName(), objectInfo.objectName(),
                fileIdWrapper.fileSize, fileIdWrapper.fileId);

        return new MessagePackage<>(ErrorCode.SUCCESS, ObjectInfo.from(storage));
    }

    private FileObjectStorage saveLocation(String bucketName,
                                           String objectName,
                                           long fileSize,
                                           String fileId) {
        FileObjectStorage storage =
                new FileObjectStorage(bucketName, fileId, objectName, fileSize);
        repository.save(storage);
        return storage;
    }

    @Override
    public MessagePackage<Void> getObjectData(ObjectInfo objectInfo,
                                              OutputStream stream) {
        validateObjectInfo(objectInfo);
        Bucket bucket = bucketService.getBucketByName(objectInfo.bucketName());
        if (bucket == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist", null);
        }
        FileObjectStorage storage = repository.getById(objectInfo.bucketName(), objectInfo.objectName());
        if (storage == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Object not exist", null);
        }

        // write
        CountDownLatch latch = new CountDownLatch(1);
        clientFileDownloadService.downloadFile(stream, storage.getFileId(),
                success -> latch.countDown());
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public MessagePackage<Void> deleteObject(ObjectInfo objectInfo) {
        validateObjectInfo(objectInfo);
        Bucket bucket = bucketService.getBucketByName(objectInfo.bucketName());
        if (bucket == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist", null);
        }
        FileObjectStorage storage = repository.getById(objectInfo.bucketName(), objectInfo.objectName());
        if (storage == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Object not exist", null);
        }
        repository.delete(storage);
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public MessagePackage<Void> clearBucketObjects(String bucketName) {
        repository.deleteByBucketName(bucketName);
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public List<ObjectInfo> getObjectsInBucket(String bucketName) {
        return repository.getObjectInfoByBucketName(bucketName);
    }


    private void validateObjectInfo(ObjectInfo objectInfo) {
        Validate.notNull(objectInfo, "ObjectInfo cannot be null");
        Validate.notEmpty(objectInfo.objectName(), "objectName cannot be null");
        Validate.notEmpty(objectInfo.bucketName(), "bucketName cannot be null");
    }

    @Override
    public void handle(ObjectInfo objectInfo) {
        repository.deleteByBucketNameAnd(objectInfo.bucketName(),
                objectInfo.objectName());
    }

    @Override
    public void handle(List<ObjectInfo> objectInfos) {
        Multimap<String, String> multimap = ArrayListMultimap.create();
        objectInfos.forEach(objectInfo ->
                multimap.put(objectInfo.bucketName(), objectInfo.objectName()));
        multimap.asMap().forEach(repository::deleteByBucketWithObjects);
    }

    @Override
    public void handleBucketDelete(String bucketName) {
        repository.deleteByBucketName(bucketName);
    }

}
