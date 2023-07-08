package org.huel.cloudhub.client.disk.domain.authentication.token;

import java.time.Duration;

/**
 * 认证令牌服务
 * <p>
 * 只负责生成Token和验证Token的合法性。不负责确认用户状态。
 *
 * @author RollW
 */
public interface AuthenticationTokenService {
    Duration DAY_7 = Duration.ofDays(7);
    Duration DAY_1 = Duration.ofDays(1);
    Duration MIN_30 = Duration.ofMinutes(30);
    Duration MIN_5 = Duration.ofMinutes(5);

    default String generateAuthToken(long userId, String signature) {
        return generateAuthToken(userId, signature, DAY_7);
    }

    String generateAuthToken(long userId, String signature,
                             Duration duration);

    /**
     * 只验证Token的合法性，不负责确认用户状态。
     */
    TokenAuthResult verifyToken(String token,
                                String signature);

    Long getUserId(String token);

}
