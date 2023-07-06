package org.huel.cloudhub.client.disk.domain.storagepermission.service;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.storagepermission.*;
import org.huel.cloudhub.client.disk.domain.storagepermission.common.StoragePermissionErrorCode;
import org.huel.cloudhub.client.disk.domain.storagepermission.common.StoragePermissionException;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionDto;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionsInfo;
import org.huel.cloudhub.client.disk.domain.storagepermission.repository.StoragePermissionRepository;
import org.huel.cloudhub.client.disk.domain.storagepermission.repository.StorageUserPermissionRepository;
import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.web.AuthErrorCode;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * @author RollW
 */
@Service
public class StoragePermissionServiceImpl implements StoragePermissionService,
        SystemResourceActionProvider {
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

    private AttributedStorage preCheckStorage(StorageIdentity storageIdentity,
                                              boolean ignoreDelete) {
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity);
        if (storage.isDeleted() && !ignoreDelete) {
            throw new StoragePermissionException(StorageErrorCode.ERROR_FILE_ALREADY_DELETED);
        }
        return storage;
    }

    private AttributedStorage preCheckStorage(StorageIdentity storageIdentity,
                                              StorageOwner storageOwner,
                                              boolean ignoreDelete) {
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        if (storage.isDeleted() && !ignoreDelete) {
            throw new StoragePermissionException(StorageErrorCode.ERROR_FILE_ALREADY_DELETED);
        }
        return storage;
    }

    @Override
    public void setStoragePermission(StorageIdentity storageIdentity,
                                     PublicPermissionType permissionType,
                                     boolean ignoreDelete) {
        AttributedStorage storage = preCheckStorage(storageIdentity, ignoreDelete);

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
    public void setStoragePermission(StorageIdentity storageIdentity,
                                     Operator operator,
                                     List<PermissionType> permissionTypes,
                                     boolean ignoreDelete) {
        AttributedStorage storage = preCheckStorage(storageIdentity, ignoreDelete);
        if (operator.getOperatorId() == storage.getOwnerId()) {
            throw new StoragePermissionException(StoragePermissionErrorCode.ERROR_PERMISSION_NOT_ALLOW_USER);
        }
        if (permissionTypes == null || permissionTypes.isEmpty()) {
            throw new StoragePermissionException(StoragePermissionErrorCode.ERROR_PERMISSION_TYPE_EMPTY);
        }
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
        List<PermissionType> reduced = reducePermissionTypes(permissionTypes);
        StorageUserPermission updated = storageUserPermission.toBuilder()
                .setPermissionTypes(reduced)
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

        List<PermissionType> reduced = reducePermissionTypes(permissionTypes);

        return StorageUserPermission.builder()
                .setStorageId(attributedStorage.getStorageId())
                .setStorageType(attributedStorage.getStorageType())
                .setUserId(operator.getOperatorId())
                .setPermissionTypes(reduced)
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
    }

    private List<PermissionType> reducePermissionTypes(List<PermissionType> permissionTypes) {
        if (permissionTypes.contains(PermissionType.DENIED)) {
            return List.of(PermissionType.DENIED);
        }
        return permissionTypes.stream().distinct().toList();
    }

    @Override
    public boolean checkPermissionOf(StorageIdentity storageIdentity,
                                     Operator operator,
                                     Action action,
                                     boolean ignoreDelete) {
        // TODO: supports inheriting permissions from parent storage
        AttributedStorage storage = preCheckStorage(storageIdentity, ignoreDelete);
        if (storage.getOwnerId() == operator.getOperatorId()) {
            // TODO: now owner could only be USER type,
            //  but in the future, needs to check the type of owner
            return true;
        }
        if (storage.getStorageId() == 0 &&
                storage.getStorageType() == StorageType.FOLDER) {
            return true;
        }

        StoragePermissionDto storagePermissionDto =
                getPermissionOf(storageIdentity, operator, ignoreDelete);
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
                                        Action action, boolean ignoreDelete) throws StoragePermissionException {
        if (!checkPermissionOf(storageIdentity, operator, action, ignoreDelete)) {
            throw new StoragePermissionException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
    }

    @Override
    public StoragePermissionDto getPermissionOf(StorageIdentity storageIdentity,
                                                Operator operator, boolean ignoreDelete) {
        AttributedStorage storage = preCheckStorage(storageIdentity, ignoreDelete);
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

    @Override
    public StoragePermissionsInfo getPermissionOf(
            StorageIdentity storageIdentity, boolean ignoreDelete) {
        AttributedStorage storage =
                preCheckStorage(storageIdentity, ignoreDelete);
        return getPermissionOf(storage);
    }

    @Override
    public StoragePermissionsInfo getPermissionOf(
            StorageIdentity storageIdentity,
            StorageOwner storageOwner,
            boolean ignoreDelete) {
        AttributedStorage storage =
                preCheckStorage(storageIdentity, storageOwner, ignoreDelete);
        return getPermissionOf(storage);
    }

    private StoragePermissionsInfo getPermissionOf(AttributedStorage storage) {
        List<StorageUserPermission> storageUserPermissions =
                storageUserPermissionRepository.getStorageUserPermissions(
                        storage.getStorageId(),
                        storage.getStorageType()
                );
        StoragePermission storagePermission =
                storagePermissionRepository.getStoragePermission(
                        storage.getStorageId(),
                        storage.getStorageType()
                );
        return StoragePermissionsInfo.of(
                storage,
                Objects.requireNonNullElseGet(storagePermission, () -> StoragePermission.defaultOf(storage)),
                storageUserPermissions
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

    @NonNull
    @Override
    public SystemAuthentication authenticate(
            SystemResource systemResource,
            Operator operator,
            Action action) {

        return switch (systemResource.getSystemResourceKind()) {
            case STORAGE_PERMISSION -> authenticationStoragePermission(
                    systemResource,
                    operator
            );
            case STORAGE_USER_PERMISSION -> authenticationStorageUserPermission(
                    systemResource,
                    operator
            );
            default ->
                    throw new IllegalArgumentException("Unsupported system resource kind: " + systemResource.getSystemResourceKind());
        };
    }

    private SystemAuthentication authenticationStoragePermission(
            SystemResource systemResource,
            Operator operator) {
        StoragePermission storagePermission =
                storagePermissionRepository.getById(systemResource.getResourceId());
        AttributedStorage attributedStorage =
                userStorageSearchService.findStorage(storagePermission);
        if (attributedStorage.getOwnerId() == operator.getOperatorId()) {
            return new SimpleSystemAuthentication(
                    storagePermission, operator, true);
        }
        return new SimpleSystemAuthentication(systemResource, operator, false);
    }

    private SystemAuthentication authenticationStorageUserPermission(
            SystemResource systemResource,
            Operator operator) {
        StorageUserPermission storageUserPermission =
                storageUserPermissionRepository.getById(systemResource.getResourceId());
        AttributedStorage attributedStorage =
                userStorageSearchService.findStorage(storageUserPermission);
        if (attributedStorage.getOwnerId() == operator.getOperatorId()) {
            return new SimpleSystemAuthentication(
                    storageUserPermission, operator, true);
        }
        return new SimpleSystemAuthentication(systemResource, operator, false);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.STORAGE_PERMISSION
                || systemResourceKind == SystemResourceKind.STORAGE_USER_PERMISSION;
    }

    @Override
    public SystemResource provide(long resourceId,
                                  SystemResourceKind systemResourceKind) {
        return switch (systemResourceKind) {
            case STORAGE_PERMISSION -> storagePermissionRepository.getById(resourceId);
            case STORAGE_USER_PERMISSION -> storageUserPermissionRepository.getById(resourceId);
            default -> throw new UnsupportedKindException(systemResourceKind);
        };
    }
}
