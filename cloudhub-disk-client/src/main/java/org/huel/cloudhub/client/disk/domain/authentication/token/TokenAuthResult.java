package org.huel.cloudhub.client.disk.domain.authentication.token;

import org.huel.cloudhub.web.AuthErrorCode;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record TokenAuthResult(
        long userId,
        String token,

        @NonNull
        AuthErrorCode errorCode) {
    public boolean success() {
        return errorCode.success();
    }

    public static TokenAuthResult success(long userId, String token) {
        return new TokenAuthResult(userId, token, AuthErrorCode.SUCCESS);
    }

    public static TokenAuthResult failure(AuthErrorCode errorCode) {
        return new TokenAuthResult(-1, null, errorCode);
    }
}
