package org.huel.cloudhub.web.data.entity;

import space.lingu.light.DataTable;

/**
 * 密码重置令牌
 *
 * @author RollW
 */
@DataTable(tableName = "password_reset_token")
public record PasswordResetToken(
        String token,
        Long userId) {
}
