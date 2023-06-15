package org.huel.cloudhub.client.disk.domain.userstats.service;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatistics;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatisticsKeys;
import org.huel.cloudhub.client.disk.domain.userstats.repository.UserStatisticsRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageEventListener;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class UserStatsStorageProcessor implements StorageEventListener {
    private final UserStatisticsRepository userStatisticsRepository;

    public UserStatsStorageProcessor(UserStatisticsRepository userStatisticsRepository) {
        this.userStatisticsRepository = userStatisticsRepository;
    }

    @Override
    public void onStorageCreated(@NonNull Storage storage,
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
