package org.huel.cloudhub.client.disk.domain.userstats;

import org.huel.cloudhub.client.disk.domain.userstats.dto.UserStatisticsDetail;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface UserStatisticsService {
    UserStatisticsDetail getUserStatistics(@NonNull StorageOwner storageOwner);
}
