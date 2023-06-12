package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceAuthenticate;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.userstats.UserDataViewService;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatisticsService;
import org.huel.cloudhub.client.disk.domain.userstats.dto.RestrictInfo;
import org.huel.cloudhub.client.disk.domain.userstats.dto.UserStatisticsDetail;
import org.huel.cloudhub.client.disk.domain.userstats.vo.UserStatisticsVo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class UserStatsController {
    private final UserStatisticsService userStatisticsService;
    private final UserDataViewService userDataViewService;

    public UserStatsController(UserStatisticsService userStatisticsService,
                               UserDataViewService userDataViewService) {
        this.userStatisticsService = userStatisticsService;
        this.userDataViewService = userDataViewService;
    }


    @GetMapping("/{ownerType}/{ownerId}/statistics")
    public HttpResponseEntity<UserStatisticsVo> getUserStatistics(
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
        UserStatisticsDetail statisticsDetail =
                userStatisticsService.getUserStatistics(storageOwner);
        return HttpResponseEntity.success(
                UserStatisticsVo.from(statisticsDetail)
        );
    }

    @SystemResourceAuthenticate(
            kindParam = "ownerType", inferredKind = false,
            idParam = "ownerId", action = Action.ACCESS, inferredAction = false)
    @GetMapping("/{ownerType}/{ownerId}/statistics/restricts")
    public HttpResponseEntity<List<RestrictInfo>> getUserRestrictInfos(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        List<RestrictInfo> restrictInfos =
                userDataViewService.findRestrictsOf(storageOwner);
        return HttpResponseEntity.success(restrictInfos);
    }

    @SystemResourceAuthenticate(
            kindParam = "ownerType", inferredKind = false,
            idParam = "ownerId",
            action = Action.ACCESS, inferredAction = false)
    @GetMapping("/{ownerType}/{ownerId}/statistics/restricts/{key}")
    public HttpResponseEntity<RestrictInfo> getUserRestrictInfoByKey(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("key") String key) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        RestrictInfo restrictInfo =
                userDataViewService.findRestrictOf(storageOwner, key);
        return HttpResponseEntity.success(restrictInfo);
    }
}
