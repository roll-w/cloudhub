package org.huel.cloudhub.client.disk.domain.user.vo;


import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;

/**
 * @author RollW
 */
public record LoginResponse(
        String token,
        UserInfo user
) {
    public static final LoginResponse NULL = new LoginResponse(null, null);

    public static LoginResponse nullResponse() {
        return NULL;
    }
}
