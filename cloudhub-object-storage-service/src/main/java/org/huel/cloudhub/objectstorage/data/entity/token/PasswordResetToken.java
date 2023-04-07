package org.huel.cloudhub.objectstorage.data.entity.token;

import org.huel.cloudhub.objectstorage.data.dto.VerifiableToken;
import space.lingu.light.*;

/**
 * 密码重置令牌
 *
 * @author RollW
 */
@DataTable(tableName = "password_reset_token", indices =
@Index(value = "token_user_id"), configuration =
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120"))
public record PasswordResetToken(
        @DataColumn(name = "token")
        @PrimaryKey
        String token,

        @DataColumn(name = "token_user_id")
        Long userId,

        @DataColumn(name = "token_expiry_time")
        Long expiryDate,

        @DataColumn(name = "token_used")
        boolean used) implements VerifiableToken {
}
