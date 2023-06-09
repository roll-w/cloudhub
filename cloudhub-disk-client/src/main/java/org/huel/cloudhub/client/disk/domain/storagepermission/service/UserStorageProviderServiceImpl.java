package org.huel.cloudhub.client.disk.domain.storagepermission.service;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermissionService;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemAuthentication;
import org.huel.cloudhub.client.disk.domain.systembased.SystemAuthentication;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceActionProvider;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Service
public class UserStorageProviderServiceImpl
        implements SystemResourceActionProvider {
    private final StoragePermissionService storagePermissionService;
    private final UserStorageSearchService userStorageSearchService;

    public UserStorageProviderServiceImpl(StoragePermissionService storagePermissionService,
                                          UserStorageSearchService userStorageSearchService) {
        this.storagePermissionService = storagePermissionService;
        this.userStorageSearchService = userStorageSearchService;
    }

    @NonNull
    @Override
    public SystemAuthentication authentication(SystemResource systemResource,
                                               Operator operator,
                                               Action action) {
        StorageIdentity storageIdentity = tryGetStorageIdentity(systemResource);
        boolean allow = storagePermissionService.checkPermissionOf(
                storageIdentity, operator,
                action, true);

        return new SimpleSystemAuthentication(systemResource, operator, allow);
    }

    private StorageIdentity tryGetStorageIdentity(SystemResource systemResource) {
        if (systemResource instanceof StorageIdentity storageIdentity) {
            return storageIdentity;
        }
        StorageType storageType = StorageType.from(systemResource.getSystemResourceKind());
        if (storageType == null) {
            return null;
        }
        return new SimpleStorageIdentity(systemResource.getResourceId(), storageType);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FILE ||
                systemResourceKind == SystemResourceKind.FOLDER;
    }

    @Override
    public SystemResource provide(long resourceId,
                                  SystemResourceKind systemResourceKind) {
        StorageType storageType = StorageType.from(systemResourceKind);
        if (storageType == null) {
            throw new IllegalArgumentException("Unsupported system resource kind: " +
                    systemResourceKind);
        }
        return userStorageSearchService.findStorage(new SimpleStorageIdentity(resourceId, storageType));
    }
}
