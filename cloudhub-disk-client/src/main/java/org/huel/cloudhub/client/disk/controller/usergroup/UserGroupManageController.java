package org.huel.cloudhub.client.disk.controller.usergroup;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupSearchService;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupService;
import org.huel.cloudhub.client.disk.domain.usergroup.dto.UserGroupInfo;
import org.huel.cloudhub.client.disk.domain.usergroup.vo.UserGroupVo;
import org.huel.cloudhub.client.disk.system.pages.PageableInterceptor;
import org.huel.cloudhub.web.HttpResponseEntity;
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

    public UserGroupManageController(UserGroupService userGroupService,
                                     UserGroupSearchService userGroupSearchService,
                                     PageableInterceptor pageableInterceptor) {
        this.userGroupService = userGroupService;
        this.userGroupSearchService = userGroupSearchService;
        this.pageableInterceptor = pageableInterceptor;
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
            @PathVariable Long id) {
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroup(id);

        return HttpResponseEntity.success(UserGroupVo.from(userGroupInfo));
    }

    @DeleteMapping("/groups/{id}")
    public HttpResponseEntity<Void> deleteUserGroup(Long id) {
        return HttpResponseEntity.success();
    }
}
