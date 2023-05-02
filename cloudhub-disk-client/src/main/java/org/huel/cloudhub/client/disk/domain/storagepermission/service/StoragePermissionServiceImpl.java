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
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
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

    public StoragePermissionServiceImpl(StoragePermissionRepository storagePermissionRepository,
                                        StorageUserPermissionRepository storageUserPermissionRepository) {
        this.storagePermissionRepository = storagePermissionRepository;
        this.storageUserPermissionRepository = storageUserPermissionRepository;
    }

    @Override
    public void setStoragePermission(Storage storage,
                                     PublicPermissionType permissionType) {
        StoragePermission storagePermission = tryFindPermission(storage);
        if (storagePermission != null) {
            StoragePermission updated = storagePermission.toBuilder()
                    .setPermissionType(permissionType)
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            storagePermissionRepository.update(updated);
            OperationContextHolder.getContext()
                    .addSystemResource(updated)
                    .addSystemResource(storage);
            return;
        }
        StoragePermission.Builder builder = StoragePermission.builder();
        StoragePermission newStoragePermission = builder
                .setStorageId(storage.getStorageId())
                .setStorageType(storage.getStorageType())
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
                .addSystemResource(storage);
    }

    @Override
    public void setStoragePermission(Storage storage, Operator operator,
                                     List<PermissionType> permissionTypes) {
        StorageUserPermission storageUserPermission = storageUserPermissionRepository.getByStorageIdAndUserId(
                storage.getStorageId(),
                storage.getStorageType(),
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
                    .addSystemResource(storage);
            return;
        }
        StorageUserPermission updated = storageUserPermission.toBuilder()
                .setPermissionTypes(permissionTypes)
                .setUpdateTime(System.currentTimeMillis())
                .build();
        storageUserPermissionRepository.update(updated);
        OperationContextHolder.getContext()
                .addSystemResource(updated)
                .addSystemResource(storage);
    }

    private StorageUserPermission buildUserPermission(Storage storage, Operator operator,
                                                      List<PermissionType> permissionTypes) {
        long time = System.currentTimeMillis();
        return StorageUserPermission.builder()
                .setStorageId(storage.getStorageId())
                .setStorageType(storage.getStorageType())
                .setUserId(operator.getOperatorId())
                .setPermissionTypes(permissionTypes)
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
    }

    @Override
    public boolean checkPermissionOf(Storage storage,
                                     Operator operator,
                                     Action action) {
        // TODO: supports inheriting permissions from parent storage

        if (storage.getOwnerId() == operator.getOperatorId()) {
            // TODO: now owner could only be USER type,
            //  but in the future, needs to check the type of owner
            return true;
        }
        StoragePermissionDto storagePermissionDto =
                getPermissionOf(storage, operator);
        if (action.isRead()) {
            return storagePermissionDto.allowRead();
        }
        if (action.isWrite()) {
            return storagePermissionDto.allowWrite();
        }
        return false;
    }

    @Override
    public void checkPermissionOfThrows(Storage storage, Operator operator,
                                        Action action) throws StoragePermissionException {
        if (!checkPermissionOf(storage, operator, action)) {
            throw new StoragePermissionException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
    }

    @Override
    public StoragePermissionDto getPermissionOf(Storage storage,
                                                Operator operator) {
        StorageUserPermission storageUserPermission = tryFindUserPermission(storage, operator);
        if (storageUserPermission != null) {
            return StoragePermissionDto.of(
                    storage,
                    operator.getOperatorId(),
                    storageUserPermission.getPermissionTypes()
            );
        }
        StoragePermission storagePermission = tryFindPermission(storage);
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

    private StoragePermission tryFindPermission(Storage storage) {
        return storagePermissionRepository.getStoragePermission(
                storage.getStorageId(),
                storage.getStorageType()
        );
    }

    private StorageUserPermission tryFindUserPermission(Storage storage, Operator operator) {
        return storageUserPermissionRepository.getByStorageIdAndUserId(
                storage.getStorageId(),
                storage.getStorageType(),
                operator.getOperatorId()
        );
    }
}
