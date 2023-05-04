package org.huel.cloudhub.client.disk.domain.share;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;

import java.time.Duration;

/**
 * @author RollW
 */
public interface ShareService {
    String share(Storage storage, Duration time,
                 Operator operator, String password);

    void cancelShare(long shareId);


}
