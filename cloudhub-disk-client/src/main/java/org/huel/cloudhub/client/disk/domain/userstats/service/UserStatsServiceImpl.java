package org.huel.cloudhub.client.disk.domain.userstats.service;

import org.huel.cloudhub.client.disk.domain.usergroup.GroupSettingKeys;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupSearchService;
import org.huel.cloudhub.client.disk.domain.usergroup.dto.UserGroupInfo;
import org.huel.cloudhub.client.disk.domain.userstats.*;
import org.huel.cloudhub.client.disk.domain.userstats.dto.RestrictInfo;
import org.huel.cloudhub.client.disk.domain.userstats.dto.UserStatisticsDetail;
import org.huel.cloudhub.client.disk.domain.userstats.repository.UserStatisticsRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserStatsServiceImpl implements UserStatisticsService, UserDataViewService {
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserGroupSearchService userGroupSearchService;

    public UserStatsServiceImpl(UserStatisticsRepository userStatisticsRepository,
                                UserGroupSearchService userGroupSearchService) {
        this.userStatisticsRepository = userStatisticsRepository;
        this.userGroupSearchService = userGroupSearchService;
    }

    @Override
    public UserStatisticsDetail getUserStatistics(
            @NonNull StorageOwner storageOwner) {
        UserStatistics userStatistics = userStatisticsRepository
                .getByOwnerIdAndOwnerType(storageOwner.getOwnerId(),
                        storageOwner.getOwnerType());
        if (userStatistics == null) {
            return UserStatisticsDetail.defaultOf(storageOwner);
        }
        return UserStatisticsDetail.from(userStatistics);
    }

    @Override
    public void rescanUserStatisticsOf(@NonNull StorageOwner storageOwner) {
        // TODO: rescan user statistics
    }

    @Override
    public void rescanUserStatistics() {

    }

    @Override
    public RestrictInfo findRestrictOf(StorageOwner storageOwner,
                                       String key) {
        UserStatisticsDetail userStatisticsDetail =
                getUserStatistics(storageOwner);
        Number valueGet = userStatisticsDetail.statistics()
                .getOrDefault(key, 0L);
        long userValue = valueGet.longValue();
        RestrictKey restrictKey = UserStatisticsKeys.restrictKeyOf(key);
        if (restrictKey == null) {
            return null;
        }
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        String restrictValue =
                userGroupInfo.settings().get(restrictKey.getRestrictKey());
        long value = restrictKey.toValue(restrictValue);
        return new RestrictInfo(key, userValue, value);
    }

    @Override
    public List<RestrictInfo> findRestrictsOf(StorageOwner storageOwner) {
        UserStatisticsDetail userStatisticsDetail =
                getUserStatistics(storageOwner);
        List<RestrictKey> restrictKeys =
                userStatisticsDetail.statistics().keySet().stream()
                        .map(UserStatisticsKeys::restrictKeyOf)
                        .toList();
        UserGroupInfo userGroupInfo =
                userGroupSearchService.findUserGroupsByUser(storageOwner);
        return restrictKeys.stream().map(restrictKey -> {
            String defaultValue = GroupSettingKeys.DEFAULT.getSettings()
                    .get(restrictKey.getRestrictKey());
            String restrictValue = userGroupInfo.settings()
                    .getOrDefault(restrictKey.getRestrictKey(), defaultValue);
            long value = restrictKey.toValue(restrictValue);
            Number number = userStatisticsDetail.statistics()
                    .getOrDefault(restrictKey.getKey(), 1L);
            long userValue = number.longValue();
            return new RestrictInfo(restrictKey.getKey(), userValue, value);
        }).toList();
    }
}
