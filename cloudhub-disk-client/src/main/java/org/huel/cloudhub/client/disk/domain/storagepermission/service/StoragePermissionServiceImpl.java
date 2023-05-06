package org.huel.cloudhub.client.disk.domain.storagepermission.service;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.storagepermission.PermissionType;
import org.huel.cloudhub.client.disk.domain.storagepermission.PublicPermissionType;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermission;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermissionService;
import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;
import org.huel.cloudhub.client.disk.domain.storagepermission.common.StoragePermissionException;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionDto;
import org.huel.cloudhub.client.disk.domain.storagepermission.repository.StoragePermissionRepository;
import org.huel.cloudhub.client.disk.domain.storagepermission.repository.StorageUserPermissionRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.web.AuthErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class StoragePermissionServiceImpl implements StoragePermissionService {
    private final StoragePermissionRepository storagePermissionRepository;
    private final StorageUserPermissionRepository storageUserPermissionRepository;
    private final UserStorageSearchService userStorageSearchService;

    public StoragePermissionServiceImpl(StoragePermissionRepository storagePermissionRepository,
                                        StorageUserPermissionRepository storageUserPermissionRepository,
                                        UserStorageSearchService userStorageSearchService) {
        this.storagePermissionRepository = storagePermissionRepository;
        this.storageUserPermissionRepository = storageUserPermissionRepository;
        this.userStorageSearchService = userStorageSearchService;
    }

    private AttributedStorage preCheckStorage(StorageIdentity storageIdentity) {
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity);
        if (storage.isDeleted()) {
            throw new StoragePermissionException(StorageErrorCode.ERROR_FILE_ALREADY_DELETED);
        }
        return storage;
    }

    @Override
    public void setStoragePermission(StorageIdentity storageIdentity,
                                     PublicPermissionType permissionType) {
        AttributedStorage storage = preCheckStorage(storageIdentity);

        StoragePermission storagePermission = tryFindPermission(storage);
        if (storagePermission != null) {
            StoragePermission updated = storagePermission.toBuilder()
                    .setPermissionType(permissionType)
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            storagePermissionRepository.update(updated);
            OperationContextHolder.getContext()
                    .addSystemResource(updated)
                    .addSystemResource(storageIdentity);
            return;
        }
        StoragePermission.Builder builder = StoragePermission.builder();
        StoragePermission newStoragePermission = builder
                .setStorageId(storageIdentity.getStorageId())
                .setStorageType(storageIdentity.getStorageType())
                .setPermissionType(permissionType)
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .build();
        long id = storagePermissionRepository.insert(newStoragePermission);
        StoragePermission inserted = builder
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .addSystemResource(storageIdentity);
    }

    @Override
    public void setStoragePermission(StorageIdentity storageIdentity, Operator operator,
                                     List<PermissionType> permissionTypes) {
        AttributedStorage storage = preCheckStorage(storageIdentity);

        StorageUserPermission storageUserPermission = storageUserPermissionRepository.getByStorageIdAndUserId(
                storageIdentity.getStorageId(),
                storageIdentity.getStorageType(),
                operator.getOperatorId()
        );
        if (storageUserPermission == null) {
            StorageUserPermission built = buildUserPermission(storage, operator, permissionTypes);
            long id = storageUserPermissionRepository.insert(built);
            StorageUserPermission inserted = built.toBuilder()
                    .setId(id)
                    .build();
            OperationContextHolder.getContext()
                    .addSystemResource(inserted)
                    .addSystemResource(storageIdentity);
            return;
        }
        StorageUserPermission updated = storageUserPermission.toBuilder()
                .setPermissionTypes(permissionTypes)
                .setUpdateTime(System.currentTimeMillis())
                .build();
        storageUserPermissionRepository.update(updated);
        OperationContextHolder.getContext()
                .addSystemResource(updated)
                .addSystemResource(storageIdentity);
    }

    private StorageUserPermission buildUserPermission(AttributedStorage attributedStorage,
                                                      Operator operator,
                                                      List<PermissionType> permissionTypes) {
        long time = System.currentTimeMillis();
        return StorageUserPermission.builder()
                .setStorageId(attributedStorage.getStorageId())
                .setStorageType(attributedStorage.getStorageType())
                .setUserId(operator.getOperatorId())
                .setPermissionTypes(permissionTypes)
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
    }

    @Override
    public boolean checkPermissionOf(StorageIdentity storageIdentity,
                                     Operator operator,
                                     Action action) {
        // TODO: supports inheriting permissions from parent storage
        AttributedStorage storage = preCheckStorage(storageIdentity);
        if (storage.getOwnerId() == operator.getOperatorId()) {
            // TODO: now owner could only be USER type,
            //  but in the future, needs to check the type of owner
            return true;
        }
        StoragePermissionDto storagePermissionDto =
                getPermissionOf(storageIdentity, operator);
        if (action.isRead()) {
            return storagePermissionDto.allowRead();
        }
        if (action.isWrite()) {
            return storagePermissionDto.allowWrite();
        }
        return false;
    }

    @Override
    public void checkPermissionOrThrows(StorageIdentity storageIdentity, Operator operator,
                                        Action action) throws StoragePermissionException {
        if (!checkPermissionOf(storageIdentity, operator, action)) {
            throw new StoragePermissionException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
    }

    @Override
    public StoragePermissionDto getPermissionOf(StorageIdentity storageIdentity,
                                                Operator operator) {
        AttributedStorage storage = preCheckStorage(storageIdentity);
        StorageUserPermission storageUserPermission = tryFindUserPermission(storage, operator);
        if (storageUserPermission != null) {
            return StoragePermissionDto.of(
                    storage,
                    operator.getOperatorId(),
                    storageUserPermission.getPermissionTypes()
            );
        }
        StoragePermission storagePermission = tryFindPermission(storageIdentity);
        if (storagePermission != null) {
            return StoragePermissionDto.of(
                    storage,
                    operator.getOperatorId(),
                    List.of(),
                    storagePermission.getPermissionType()
            );
        }
        return StoragePermissionDto.of(
                storage,
                operator.getOperatorId(),
                List.of(),
                PublicPermissionType.PRIVATE
        );
    }

    private StoragePermission tryFindPermission(StorageIdentity storage) {
        return storagePermissionRepository.getStoragePermission(
                storage.getStorageId(),
                storage.getStorageType()
        );
    }

    private StorageUserPermission tryFindUserPermission(StorageIdentity storage, Operator operator) {
        return storageUserPermissionRepository.getByStorageIdAndUserId(
                storage.getStorageId(),
                storage.getStorageType(),
                operator.getOperatorId()
        );
    }
}
