package org.huel.cloudhub.client.disk.domain.user.filter;

import org.huel.cloudhub.web.ErrorCode;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface UserInfoFilter {
    @NonNull
    ErrorCode filter(@NonNull UserFilteringInfo userFilteringInfo);
}
