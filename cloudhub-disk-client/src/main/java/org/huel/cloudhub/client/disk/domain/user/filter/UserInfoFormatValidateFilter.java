package org.huel.cloudhub.client.disk.domain.user.filter;

import org.huel.cloudhub.client.disk.domain.user.common.UserChecker;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import org.huel.cloudhub.web.UserErrorCode;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class UserInfoFormatValidateFilter implements UserInfoFilter {
    @NonNull
    @Override
    public ErrorCode filter(@NonNull UserFilteringInfo userFilteringInfo) {
        return switch (userFilteringInfo.filterType()) {
            case USERNAME -> UserChecker.checkUsername(userFilteringInfo.value())
                    ? CommonErrorCode.SUCCESS
                    : UserErrorCode.ERROR_USERNAME_NON_COMPLIANCE;
            case PASSWORD -> UserChecker.checkPassword(userFilteringInfo.value())
                    ? CommonErrorCode.SUCCESS
                    : UserErrorCode.ERROR_PASSWORD_NON_COMPLIANCE;
            case EMAIL -> UserChecker.checkEmail(userFilteringInfo.value())
                    ? CommonErrorCode.SUCCESS
                    : UserErrorCode.ERROR_EMAIL_NON_COMPLIANCE;
            case NICKNAME ->  checkNickname(userFilteringInfo.value())
                    ? CommonErrorCode.SUCCESS
                    : UserErrorCode.ERROR_NICKNAME_NON_COMPLIANCE;
            // TODO: phone number & other check
            default -> CommonErrorCode.SUCCESS;
        };
    }

    private boolean checkNickname(String nickname) {
        return nickname.length() <= 20 && nickname.length() >= 1;
    }
}
