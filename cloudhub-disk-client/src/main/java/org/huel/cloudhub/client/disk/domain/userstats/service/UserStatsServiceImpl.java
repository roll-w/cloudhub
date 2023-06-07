package org.huel.cloudhub.client.disk.domain.userstats.service;

import org.huel.cloudhub.client.disk.domain.userstats.UserStatistics;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatisticsService;
import org.huel.cloudhub.client.disk.domain.userstats.dto.UserStatisticsDetail;
import org.huel.cloudhub.client.disk.domain.userstats.repository.UserStatisticsRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Service
public class UserStatsServiceImpl implements UserStatisticsService {
    private final UserStatisticsRepository userStatisticsRepository;

    public UserStatsServiceImpl(UserStatisticsRepository userStatisticsRepository) {
        this.userStatisticsRepository = userStatisticsRepository;
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
}
