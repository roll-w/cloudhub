package org.huel.cloudhub.client.disk.controller.usergroup;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.controller.usergroup.vo.UserGroupCreateRequest;
import org.huel.cloudhub.client.disk.controller.usergroup.vo.UserGroupMemberCreateRequest;
import org.huel.cloudhub.client.disk.controller.usergroup.vo.UserGroupMemberVo;
import org.huel.cloudhub.client.disk.controller.usergroup.vo.UserGroupUpdateRequest;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperatorProvider;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupOperator;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupSearchService;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupService;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupException;
import org.huel.cloudhub.client.disk.domain.usergroup.dto.UserGroupInfo;
import org.huel.cloudhub.client.disk.domain.usergroup.vo.UserGroupVo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.system.pages.PageableInterceptor;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.KeyValue;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class UserGroupManageController {
    private final UserGroupService userGroupService;
    private final UserGroupSearchService userGroupSearchService;
    private final PageableInterceptor pageableInterceptor;
    private final ContextThreadAware<PageableContext> pageableContextAware;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;
    private final UserSearchService userSearchService;

    public UserGroupManageController(UserGroupService userGroupService,
                                     UserGroupSearchService userGroupSearchService,
                                     PageableInterceptor pageableInterceptor,
                                     ContextThreadAware<PageableContext> pageableContextAware,
                                     SystemResourceOperatorProvider systemResourceOperatorProvider,
                                     UserSearchService userSearchService) {
        this.userGroupService = userGroupService;
        this.userGroupSearchService = userGroupSearchService;
        this.pageableInterceptor = pageableInterceptor;
        this.pageableContextAware = pageableContextAware;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
        this.userSearchService = userSearchService;
    }

    @PostMapping("/groups")
    public HttpResponseEntity<Void> createUserGroup(
            @RequestBody UserGroupCreateRequest userGroupCreateRequest) {
        userGroupService.createUserGroup(
                userGroupCreateRequest.name(),
                userGroupCreateRequest.description()
        );
        return HttpResponseEntity.success();
    }

    @GetMapping("/groups")
    public HttpResponseEntity<List<UserGroupVo>> getUserGroups(Pageable pageable) {
        List<UserGroupInfo> userGroupInfos =
                userGroupSearchService.getUserGroups(pageable);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        userGroupInfos.stream()
                                .map(UserGroupVo::from)
                                .toList(),
                        pageable,
                        UserGroup.class
                )
        );
    }

    @GetMapping("/groups/{id}")
    public HttpResponseEntity<UserGroupVo> getUserGroupDetails(
            @PathVariable("id") Long id) {
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroup(id);

        return HttpResponseEntity.success(UserGroupVo.from(userGroupInfo));
    }

    @GetMapping("/groups/{id}/members")
    public HttpResponseEntity<List<UserGroupMemberVo>> getUserGroupMembers(
            @PathVariable("id") Long id) {
        List<? extends StorageOwner> members =
                userGroupSearchService.findUserGroupMembers(id);
        List<Long> userIds = members.stream()
                .filter(storageOwner -> storageOwner.getOwnerType() == LegalUserType.USER)
                .map(StorageOwner::getOwnerId)
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);
        return HttpResponseEntity.success(
                attributedUsers.stream()
                        .map(UserGroupMemberVo::from)
                        .toList()
        );


    }

    @PutMapping("/groups/{id}/members")
    public HttpResponseEntity<Void> addUserGroupMember(
            @PathVariable("id") Long id,
            @RequestBody UserGroupMemberCreateRequest request) {
        if (request.type() != LegalUserType.USER) {
            throw new UserGroupException(WebCommonErrorCode.ERROR_PARAM_FAILED);
        }

        AttributedUser user = userSearchService.tryFindUser(request.name());
        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator.addMember(user);

        return HttpResponseEntity.success();
    }

    @PutMapping("/groups/{id}")
    public HttpResponseEntity<Void> updateUserGroup(
            @PathVariable("id") Long id,
            @RequestBody UserGroupUpdateRequest userGroupUpdateRequest) {

        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator.disableAutoUpdate()
                .setName(userGroupUpdateRequest.name())
                .setDescription(userGroupUpdateRequest.description())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/groups/{id}/settings")
    public HttpResponseEntity<Void> setSettingOfGroup(
            @PathVariable("id") Long id,
            @RequestBody KeyValue keyValue) {
        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator
                .enableAutoUpdate()
                .setSetting(keyValue.key(), keyValue.value());
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/groups/{id}")
    public HttpResponseEntity<Void> deleteUserGroup(
            @PathVariable("id") Long id) {
        UserGroupOperator userGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(id, SystemResourceKind.USER_GROUP)
        );
        userGroupOperator.delete();
        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/groups")
    public HttpResponseEntity<UserGroupVo> getGroupSettings(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId
    ) {
        StorageOwner storageOwner =
                ParameterHelper.buildStorageOwner(ownerId, ownerType);
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        return HttpResponseEntity.success(
                UserGroupVo.from(userGroupInfo)
        );
    }
}
