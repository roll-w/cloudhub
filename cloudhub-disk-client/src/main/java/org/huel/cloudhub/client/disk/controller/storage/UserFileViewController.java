package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageCategoryService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.controller.storage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class UserFileViewController {
    private final StorageCategoryService storageCategoryService;
    private final UserStorageSearchService storageSearchService;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;
    private final SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory;

    public UserFileViewController(StorageCategoryService storageCategoryService,
                                  UserStorageSearchService storageSearchService,
                                  ContextThreadAware<PageableContext> pageableContextThreadAware,
                                  SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory) {
        this.storageCategoryService = storageCategoryService;
        this.storageSearchService = storageSearchService;
        this.pageableContextThreadAware = pageableContextThreadAware;
        this.systemResourceAuthenticationProviderFactory = systemResourceAuthenticationProviderFactory;
    }


    @GetMapping("/{ownerType}/{ownerId}/disk/storages")
    public HttpResponseEntity<List<StorageVo>> getAllFiles(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerType) {
        StorageOwner storageOwner =
                ParameterHelper.buildStorageOwner(ownerId, ownerType);
        UserIdentity user = ApiContextHolder.getContext().userInfo();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        pageableContext.setIncludeDeleted(false);
        List<AttributedStorage> attributedStorages =
                storageSearchService.listStorages(storageOwner);

        SystemResourceAuthenticationProvider authenticationProvider = systemResourceAuthenticationProviderFactory
                .getSystemResourceAuthenticationProvider(SystemResourceKind.FILE);
        List<SystemAuthentication> authentications =
                authenticationProvider.authenticate(attributedStorages, user, Action.ACCESS);
        List<AttributedStorage> authenticatedStorages =
                authentications.stream().filter(SystemAuthentication::isAllowAccess)
                        .map(SystemAuthentication::getSystemResource)
                        .map(resource -> resource.cast(AttributedStorage.class))
                        .toList();
        List<StorageVo> storageVos = authenticatedStorages.stream()
                .map(StorageVo::from)
                .toList();
        return HttpResponseEntity.success(
                pageableContext.toPage(storageVos)
        );
    }
}
