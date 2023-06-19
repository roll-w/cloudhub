package org.huel.cloudhub.client.disk.controller.usergroup;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupSearchService;
import org.huel.cloudhub.client.disk.domain.usergroup.dto.UserGroupInfo;
import org.huel.cloudhub.client.disk.domain.usergroup.vo.UserGroupVo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author RollW
 */
@Api
public class UserGroupController {
    private final UserGroupSearchService userGroupSearchService;

    public UserGroupController(UserGroupSearchService userGroupSearchService) {
        this.userGroupSearchService = userGroupSearchService;
    }

    // Gets the current user's group settings
    @GetMapping("/groups")
    public HttpResponseEntity<UserGroupVo> getCurrentUserGroupSettings() {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(userIdentity);
        return HttpResponseEntity.success(
                UserGroupVo.from(userGroupInfo));
    }

    @GetMapping("/{ownerType}/{ownerId}/groups")
    public HttpResponseEntity<UserGroupVo> getGroupSettings(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId
    ) {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        if (userIdentity.getUserId() != storageOwner.getOwnerId()) {
            // TODO: supports other user types in the future
            throw new AuthenticationException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }

        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        return HttpResponseEntity.success(
                UserGroupVo.from(userGroupInfo));
    }
}
