package org.huel.cloudhub.client.disk.domain.user.filter;

import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class UserInfoSensitiveWordFilter implements UserInfoFilter {

    @NonNull
    @Override
    public ErrorCode filter(@NonNull UserFilteringInfo userFilteringInfo) {
        // TODO: sensitive words filter
        return CommonErrorCode.SUCCESS;
    }
}
