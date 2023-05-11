package org.huel.cloudhub.client.disk.domain.user;

import org.huel.cloudhub.client.disk.domain.user.dto.LoginLog;
import org.huel.cloudhub.web.data.page.Pageable;

import java.util.List;

/**
 * @author RollW
 */
public interface LoginLogService {
    List<LoginLog> getLogs(Pageable pageable);

    List<LoginLog> getUserLogs(long userId, Pageable pageable);

    long getLogsCount();

    long getUserLogsCount(long userId);
}
