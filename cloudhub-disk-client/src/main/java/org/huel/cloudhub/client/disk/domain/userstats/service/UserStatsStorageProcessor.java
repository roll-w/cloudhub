package org.huel.cloudhub.client.disk.domain.userstats.service;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstats.UserDataViewService;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatistics;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatisticsKeys;
import org.huel.cloudhub.client.disk.domain.userstats.dto.RestrictInfo;
import org.huel.cloudhub.client.disk.domain.userstats.repository.UserStatisticsRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageEventListener;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileAttributesInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class UserStatsStorageProcessor implements StorageEventListener {
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserDataViewService userDataViewService;

    public UserStatsStorageProcessor(UserStatisticsRepository userStatisticsRepository,
                                     UserDataViewService userDataViewService) {
        this.userStatisticsRepository = userStatisticsRepository;
        this.userDataViewService = userDataViewService;
    }

    // TODO: only preserve pre-checks of storage creation,
    //  move the statistics methods to the new job APIs.

    @Override
    public ErrorCode onBeforeStorageCreated(@NonNull StorageOwner storageOwner,
                                            @NonNull Operator operator,
                                            @Nullable FileAttributesInfo fileAttributesInfo) {
        List<RestrictInfo> restrictInfos =
                userDataViewService.findRestrictsOf(storageOwner);
        for (RestrictInfo restrictInfo : restrictInfos) {
            if (restrictInfo.restrict() == UserStatisticsKeys.NO_LIMIT) {
                continue;
            }
            ErrorCode errorCode = checkRestrictOf(restrictInfo, fileAttributesInfo);
            if (errorCode.failed()) {
                return errorCode;
            }
        }

        return CommonErrorCode.SUCCESS;
    }

    private ErrorCode checkRestrictOf(RestrictInfo restrictInfo,
                                      FileAttributesInfo fileAttributesInfo) {
        if (restrictInfo.restrict() == UserStatisticsKeys.NO_LIMIT) {
            return CommonErrorCode.SUCCESS;
        }
        switch (restrictInfo.key()) {
            case UserStatisticsKeys.USER_STORAGE_USED -> {
                if (restrictInfo.restrict() < fileAttributesInfo.size() + restrictInfo.value()) {
                    return StorageErrorCode.ERROR_STORAGE_SIZE_LIMIT;
                }
            }
            case UserStatisticsKeys.USER_STORAGE_COUNT -> {
                if (restrictInfo.restrict() < restrictInfo.value() + 1) {
                    return StorageErrorCode.ERROR_STORAGE_COUNT_LIMIT;
                }
            }
        }

        return CommonErrorCode.SUCCESS;
    }

    @Override
    public void onStorageCreated(@NonNull AttributedStorage storage,
                                 StorageAttr storageAttr) {
        LegalUserType userType = storage.getOwnerType();
        long userId = storage.getOwnerId();

        UserStatistics userStatistics =
                userStatisticsRepository.getByUserId(userId, userType);
        updateUserStatistics(userStatistics, storage, storageAttr);

    }

    private void updateUserStatistics(UserStatistics userStatistics,
                                      Storage storage,
                                      StorageAttr storageAttr) {
        if (userStatistics == null) {
            createUserStatistics(storage, storageAttr);
            return;
        }
        if (userStatistics.getStatistics().isEmpty()) {
            Map<String, Long> stats =
                    updateStorageStatistics(new HashMap<>(), storageAttr);
            UserStatistics updated = userStatistics.toBuilder()
                    .setStatistics(stats)
                    .build();
            userStatisticsRepository.update(updated);
            return;
        }
        updateStorageStatistics(userStatistics.getStatistics(), storageAttr);
        userStatisticsRepository.update(userStatistics);
    }

    private void createUserStatistics(StorageOwner storageOwner,
                                      StorageAttr storageAttr) {
        Map<String, Long> stats = new HashMap<>();
        updateStorageStatistics(stats, storageAttr);
        UserStatistics userStatistics = UserStatistics.builder()
                .setUserId(storageOwner.getOwnerId())
                .setUserType(storageOwner.getOwnerType())
                .setStatistics(stats)
                .build();
        userStatisticsRepository.insert(userStatistics);
    }

    private Map<String, Long> updateStorageStatistics(Map<String, Long> stats,
                                                      StorageAttr storageAttr) {
        long totalSize = getByKey(stats, UserStatisticsKeys.USER_STORAGE_USED);
        long totalStorageCount = getByKey(stats, UserStatisticsKeys.USER_STORAGE_COUNT);

        stats.put(UserStatisticsKeys.USER_STORAGE_USED,
                totalSize + storageAttr.size());
        stats.put(UserStatisticsKeys.USER_STORAGE_COUNT,
                totalStorageCount + 1);

        return stats;
    }

    private long getByKey(Map<String, Long> stats,
                          String key) {
        Number value = stats.get(key);
        if (value == null) {
            return 0;
        }
        return value.longValue();
    }
}
