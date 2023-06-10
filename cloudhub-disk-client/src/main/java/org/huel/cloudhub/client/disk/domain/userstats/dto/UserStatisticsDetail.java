package org.huel.cloudhub.client.disk.domain.userstats.dto;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatistics;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

import java.util.Map;

/**
 * @author RollW
 */
public record UserStatisticsDetail(
        long id,
        long userId,
        LegalUserType userType,
        Map<String, Long> statistics
) {

    public static UserStatisticsDetail from(UserStatistics userStatistics) {
        return new UserStatisticsDetail(
                userStatistics.getId(),
                userStatistics.getUserId(),
                userStatistics.getUserType(),
                userStatistics.getStatistics()
        );
    }

    public static UserStatisticsDetail defaultOf(StorageOwner storageOwner) {
        return new UserStatisticsDetail(
                0L,
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType(),
                Map.of()
        );
    }
}
