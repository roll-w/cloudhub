package org.huel.cloudhub.client.data.entity.token;

import org.huel.cloudhub.client.data.dto.VerifiableToken;
import space.lingu.light.DataTable;

/**
 * 密码重置令牌
 *
 * @author RollW
 */
@DataTable(tableName = "password_reset_token")
public record PasswordResetToken (
        String token,
        Long userId) implements VerifiableToken {
}
