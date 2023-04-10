package org.huel.cloudhub.client.disk.domain.authentication.token;

/**
 * 认证令牌服务
 * <p>
 * 只负责生成Token和验证Token的合法性。不负责确认用户状态。
 *
 * @author RollW
 */
public interface AuthenticationTokenService {
    String generateAuthToken(long userId, String signature);

    /**
     * 只验证Token的合法性，不负责确认用户状态。
     */
    TokenAuthResult verifyToken(String token, String signature);

    Long getUserId(String token);

    void setTokenExpireTime(long expireTimeInSecond);
}
