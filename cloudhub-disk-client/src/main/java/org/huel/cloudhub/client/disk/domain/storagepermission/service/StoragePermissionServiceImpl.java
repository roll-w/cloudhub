package org.huel.cloudhub.client.disk.domain.storagepermission.service;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.storagepermission.*;
import org.huel.cloudhub.client.disk.domain.storagepermission.common.StoragePermissionException;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionDto;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionsInfo;
import org.huel.cloudhub.client.disk.domain.storagepermission.repository.StoragePermissionRepository;
import org.huel.cloudhub.client.disk.domain.storagepermission.repository.StorageUserPermissionRepository;
import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
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
        SystemResourceActionProvider, SystemResourceOperatorFactory, StoragePermissionActionDelegate {
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
        if (storageUserPermission != null && !storageUserPermission.isDeleted()) {
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
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return StoragePermissionAction.class.isAssignableFrom(clazz);
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         boolean checkDelete) {
        return switch (systemResource.getSystemResourceKind()) {
            case STORAGE_PERMISSION -> openStoragePermissionOperator(
                    systemResource,
                    checkDelete
            );
            default -> throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        };
    }

    private SystemResourceOperator openStoragePermissionOperator(
            SystemResource systemResource,
            boolean checkDelete) {
        return switch (systemResource.getSystemResourceKind()) {
            case FILE, FOLDER -> {
                StorageIdentity storageIdentity = SimpleStorageIdentity.of(
                        systemResource
                );
                AttributedStorage attributedStorage =
                        userStorageSearchService.findStorage(storageIdentity);
                StoragePermission storagePermission =
                        storagePermissionRepository.getStoragePermission(
                                attributedStorage.getStorageId(),
                                attributedStorage.getStorageType()
                        );
                yield new StoragePermissionOperatorImpl(
                        this,
                        attributedStorage,
                        storagePermission,
                        checkDelete
                );
            }
            case STORAGE_PERMISSION -> {
                StoragePermission storagePermission =
                        storagePermissionRepository.getById(systemResource.getResourceId());
                AttributedStorage attributedStorage =
                        userStorageSearchService.findStorage(storagePermission);
                yield new StoragePermissionOperatorImpl(
                        this,
                        attributedStorage,
                        storagePermission,
                        checkDelete
                );
            }
            default -> throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        };
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         SystemResourceKind targetSystemResourceKind,
                                                         boolean checkDelete) {
        if (targetSystemResourceKind == SystemResourceKind.STORAGE_PERMISSION) {
            return openStoragePermissionOperator(
                    systemResource,
                    checkDelete
            );
        }
        throw new UnsupportedKindException(targetSystemResourceKind);
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


    @Override
    public void updateStoragePermission(StoragePermission permission) {
        storagePermissionRepository.update(permission);
    }

    @Override
    public void updateUserStoragePermission(StorageUserPermission permission) {
        storageUserPermissionRepository.update(permission);
    }

    @Override
    public StorageUserPermission getUserStoragePermission(Operator operator, StorageIdentity storageIdentity) {
        return storageUserPermissionRepository.getByStorageIdAndUserId(
                storageIdentity.getStorageId(),
                storageIdentity.getStorageType(),
                operator.getOperatorId()
        );
    }

    @Override
    public long createUserStoragePermission(StorageUserPermission storageUserPermission) {
        return storageUserPermissionRepository.insert(storageUserPermission);
    }

    @Override
    public long createStoragePermission(StoragePermission permission) {
        return storagePermissionRepository.insert(permission);
    }
}
