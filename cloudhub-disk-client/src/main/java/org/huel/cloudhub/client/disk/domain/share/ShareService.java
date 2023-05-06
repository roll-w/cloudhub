package org.huel.cloudhub.client.disk.domain.share;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;

import java.time.Duration;

/**
 * @author RollW
 */
public interface ShareService {
    Duration DAYS_1 = Duration.ofDays(1);
    Duration DAYS_7 = Duration.ofDays(7);
    Duration DAYS_30 = Duration.ofDays(30);
    Duration INFINITE = Duration.ofDays(-1);

    SharePasswordInfo share(StorageIdentity storage, Duration time,
                            Operator operator, String password);

    void cancelShare(long shareId);


}
