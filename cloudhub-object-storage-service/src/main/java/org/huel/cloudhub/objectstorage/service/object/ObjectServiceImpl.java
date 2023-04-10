package org.huel.cloudhub.objectstorage.service.object;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.huel.cloudhub.objectstorage.data.database.repository.FileObjectStorageRepository;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfo;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.objectstorage.data.entity.object.FileObjectStorage;
import org.huel.cloudhub.objectstorage.event.object.OnNewlyObjectEvent;
import org.huel.cloudhub.objectstorage.event.object.OnObjectDeleteEvent;
import org.huel.cloudhub.objectstorage.event.object.OnObjectRenameEvent;
import org.huel.cloudhub.web.IoErrorCode;
import org.huel.cloudhub.common.SystemRuntimeException;
import org.huel.cloudhub.client.CFSClient;
import org.springframework.context.ApplicationEventPublisher;
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
    private final FileObjectStorageRepository repository;
    private final String tempDir ;
    private final CFSClient cfsClient;
    private final ApplicationEventPublisher eventPublisher;

    public ObjectServiceImpl(FileObjectStorageRepository repository,
                             CFSClient cfsClient,
                             ClientConfigLoader clientConfigLoader,
                             ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.cfsClient = cfsClient;
        this.eventPublisher = eventPublisher;
        this.tempDir = clientConfigLoader.getTempFilePath();
    }

    @Override
    public ObjectInfoDto saveObject(ObjectInfo objectInfo,
                                    InputStream stream) throws IOException {
        validateObjectInfo(objectInfo);
        var fileIdWrapper = new Object() {
            String fileId = null;
            long fileSize = -1;
        };
        CountDownLatch latch = new CountDownLatch(1);
        cfsClient.uploadFile(stream, tempDir, (success, fileId, fileSize) -> {
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
            throw new SystemRuntimeException(e);
        }
        if (fileIdWrapper.fileId == null) {
            throw new ObjectRuntimeException(IoErrorCode.ERROR_FILE_UPLOAD);
        }
        FileObjectStorage storage = saveLocation(
                objectInfo.bucketName(), objectInfo.objectName(),
                fileIdWrapper.fileSize, fileIdWrapper.fileId);

        return ObjectInfoDto.from(storage);
    }

    private FileObjectStorage saveLocation(String bucketName,
                                           String objectName,
                                           long fileSize,
                                           String fileId) {
        FileObjectStorage old = repository.getById(bucketName, objectName);
        if (old != null && old.getFileId().equals(fileId)) {
            return old;
        }
        FileObjectStorage storage = new FileObjectStorage(
                bucketName, objectName, fileId,
                fileSize, System.currentTimeMillis());
        repository.save(storage);
        OnNewlyObjectEvent event =
                new OnNewlyObjectEvent(ObjectInfoDto.from(storage));
        eventPublisher.publishEvent(event);
        return storage;
    }

    @Override
    public void getObjectData(ObjectInfo objectInfo,
                              OutputStream stream) throws IOException {
        validateObjectInfo(objectInfo);
        FileObjectStorage storage = repository.getById(objectInfo.bucketName(), objectInfo.objectName());
        if (storage == null) {
            throw new ObjectRuntimeException(ObjectErrorCode.ERROR_OBJECT_NOT_EXIST);
        }

        // write
        CountDownLatch latch = new CountDownLatch(1);
        cfsClient.downloadFile(stream, storage.getFileId(),
                success -> latch.countDown());
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new SystemRuntimeException(e);
        }
    }

    @Override
    public void getObjectData(ObjectInfo objectInfo, OutputStream stream,
                              long startBytes, long endBytes) throws IOException {
        validateObjectInfo(objectInfo);
        FileObjectStorage storage = repository.getById(objectInfo.bucketName(), objectInfo.objectName());
        if (storage == null) {
            throw new ObjectRuntimeException(ObjectErrorCode.ERROR_OBJECT_NOT_EXIST);
        }
        CountDownLatch latch = new CountDownLatch(1);
        cfsClient.downloadFile(stream, storage.getFileId(),
                startBytes, endBytes, success -> latch.countDown());
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new SystemRuntimeException(e);
        }
    }

    @Override
    public void deleteObject(ObjectInfo objectInfo) {
        validateObjectInfo(objectInfo);
        FileObjectStorage storage = repository.getById(objectInfo.bucketName(), objectInfo.objectName());
        if (storage == null) {
            throw new ObjectRuntimeException(ObjectErrorCode.ERROR_OBJECT_NOT_EXIST);
        }
        OnObjectDeleteEvent event = new OnObjectDeleteEvent(ObjectInfoDto.from(storage));
        eventPublisher.publishEvent(event);
        repository.delete(storage);
    }

    @Override
    public void clearBucketObjects(String bucketName) {
        repository.deleteByBucketName(bucketName);
    }

    @Override
    public List<ObjectInfoDto> getObjectsInBucket(String bucketName) {
        return repository.getObjectInfoDtosByBucketName(bucketName);
    }

    @Override
    public ObjectInfoDto getObjectInBucket(String bucketName, String objectName) {
        return ObjectInfoDto.from(repository.getById(bucketName, objectName));
    }

    @Override
    public void setObjectFileId(String bucketName, String objectName, String fileId) {
        FileObjectStorage objectStorage = repository.getById(bucketName, objectName);
        if (objectStorage == null) {
            throw new ObjectRuntimeException(ObjectErrorCode.ERROR_OBJECT_NOT_EXIST);
        }
        objectStorage.setFileId(fileId);
        repository.update(objectStorage);
    }

    @Override
    public ObjectInfoDto renameObject(ObjectInfo oldInfo, String newName) {
        Validate.notEmpty(newName, "objectName cannot be null or empty.");
        FileObjectStorage storage =
                repository.getById(oldInfo.bucketName(), oldInfo.objectName());
        storage.setObjectName(newName);
        repository.update(storage);
        ObjectInfoDto dto = ObjectInfoDto.from(storage);
        OnObjectRenameEvent event = new OnObjectRenameEvent(oldInfo, newName);
        eventPublisher.publishEvent(event);
        return dto;
    }

    @Override
    public boolean isObjectExist(ObjectInfo objectInfo) {
        return repository.isObjectExist(objectInfo.bucketName(), objectInfo.objectName());
    }

    @Override
    public int getObjectsCount() {
        return repository.getAllObjectsCount();
    }


    private void validateObjectInfo(ObjectInfo objectInfo) {
        Validate.notNull(objectInfo, "ObjectInfo cannot be null");
        Validate.notEmpty(objectInfo.objectName(), "objectName cannot be null");
        Validate.notEmpty(objectInfo.bucketName(), "bucketName cannot be null");
    }

    @Override
    public void handleObjectRemove(ObjectInfoDto objectInfoDto) {
    }

    @Override
    public void handleObjectRemove(List<ObjectInfoDto> objectInfoDtos) {
        Multimap<String, String> multimap = ArrayListMultimap.create();
        objectInfoDtos.forEach(objectInfoDto ->
                multimap.put(objectInfoDto.bucketName(), objectInfoDto.objectName()));
        multimap.asMap().forEach(repository::deleteByBucketWithObjects);
    }

    @Override
    public void handleBucketDelete(String bucketName) {
        repository.deleteByBucketName(bucketName);
    }
}
